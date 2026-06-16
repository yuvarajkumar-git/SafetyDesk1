package com.cts.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.PermitApprovalRequest;
import com.cts.dto.request.PermitRequest;
import com.cts.dto.request.PermitUpdateRequest;
import com.cts.dto.response.PermitResponse;
import com.cts.entity.User;
import com.cts.entity.WorkPermit;
import com.cts.enums.NotificationCategory;
import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;
import com.cts.enums.Role;
import com.cts.exception.AccessForbiddenException;
import com.cts.exception.ConflictException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.PermitMapper;
import com.cts.repository.UserRepository;
import com.cts.repository.WorkPermitRepository;
import com.cts.repository.spec.PermitSpecification;
import com.cts.security.CurrentUser;
import com.cts.service.AuditLogService;
import com.cts.service.NotificationRouter;
import com.cts.service.PermitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermitServiceImpl implements PermitService {

    private final WorkPermitRepository permitRepository;
    private final UserRepository userRepository;
    private final PermitMapper permitMapper;
    private final AuditLogService auditLogService;
    private final CurrentUser currentUser;
    private final NotificationRouter notificationRouter;

    private static final String ENTITY_TYPE = "WorkPermit";
    private static final Long NO_EXCLUSION = -1L;

    @Override
    @Transactional
    public PermitResponse createPermit(PermitRequest request) {
        log.info("Creating {} permit at {}", request.getPermitType(), request.getWorkLocation());

        // IssuedTo existence is validated inside the mapper (loads or throws)
        if (!request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new IllegalArgumentException("EndDateTime must be after StartDateTime");
        }

        WorkPermit permit = permitMapper.toEntity(request);
        permit.setStatus(PermitStatus.DRAFT);

        WorkPermit saved = permitRepository.save(permit);
        auditLogService.record(currentUser.id(), "CREATE_PERMIT", ENTITY_TYPE, saved.getPermitId());

        log.info("Permit created in Draft with id: {}", saved.getPermitId());
        return permitMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PermitResponse getPermitById(Long permitId) {
        WorkPermit permit = findOrThrow(permitId);
        enforceSiteAccess(permit);
        return permitMapper.toResponse(permit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermitResponse> searchPermits(Long siteId, PermitType permitType, PermitStatus status,
                                              String workLocation, Long issuedToId, Long approvedById,
                                              LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        Long effectiveSiteId = siteId;
        if (!currentUser.hasAnyRole(Role.EHS_MANAGER, Role.COMPLIANCE_OFFICER, Role.ADMIN)) {
            effectiveSiteId = currentUser.siteId();
        }
        var spec = PermitSpecification.build(
                effectiveSiteId, permitType, status, workLocation, issuedToId, approvedById, fromDateTime, toDateTime);
        return permitRepository.findAll(spec).stream()
                .map(permitMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PermitResponse updatePermit(Long permitId, PermitUpdateRequest request) {
        WorkPermit permit = findOrThrow(permitId);
        enforceSiteAccess(permit);
        if (permit.getStatus() != PermitStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only a Draft permit can be edited. Current status: " + permit.getStatus().getLabel());
        }
        if (request.getWorkDescription() != null)   permit.setWorkDescription(request.getWorkDescription());
        if (request.getHazardsIdentified() != null) permit.setHazardsIdentified(request.getHazardsIdentified());
        if (request.getControlMeasures() != null)   permit.setControlMeasures(request.getControlMeasures());

        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(currentUser.id(), "UPDATE_PERMIT", ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse submitForApproval(Long permitId) {
        log.info("Submitting permit {} for approval", permitId);
        WorkPermit permit = findOrThrow(permitId);
        enforceSiteAccess(permit);
        validateTransition(permit.getStatus(), PermitStatus.PENDING_APPROVAL);

        if (isBlank(permit.getHazardsIdentified()) || isBlank(permit.getControlMeasures())) {
            throw new IllegalArgumentException(
                    "HazardsIdentified and ControlMeasures must be completed before submitting for approval");
        }

        permit.setStatus(PermitStatus.PENDING_APPROVAL);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(currentUser.id(), "SUBMIT_PERMIT", ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse approvePermit(Long permitId, PermitApprovalRequest request) {
        log.info("Approving permit {} by user {}", permitId, request.getApprovedById());

        WorkPermit permit = findOrThrow(permitId);
        enforceSiteAccess(permit);
        if (permit.getStatus() != PermitStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException(
                    "Only a PendingApproval permit can be approved. Current status: " + permit.getStatus().getLabel());
        }

        User approver = userRepository.findById(request.getApprovedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Approver (User) not found with id: " + request.getApprovedById()));
        if (approver.getRole() != Role.PTW_COORDINATOR
                && approver.getRole() != Role.SAFETY_OFFICER
                && approver.getRole() != Role.EHS_MANAGER) {
            throw new IllegalArgumentException(
                    "Approver must be a PTWCoordinator, SafetyOfficer, or EHSManager. User "
                            + request.getApprovedById() + " has role " + approver.getRole().getLabel());
        }

        permit.setApprovedBy(approver);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(currentUser.id(), "APPROVE_PERMIT", ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse activatePermit(Long permitId) {
        log.info("Activating permit {}", permitId);
        WorkPermit permit = findOrThrow(permitId);
        enforceSiteAccess(permit);
        validateTransition(permit.getStatus(), PermitStatus.ACTIVE);

        if (permit.getApprovedBy() == null) {
            throw new IllegalArgumentException("Permit must be approved before activation");
        }

        checkForConflicts(permit, NO_EXCLUSION);

        permit.setStatus(PermitStatus.ACTIVE);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(currentUser.id(), "ACTIVATE_PERMIT", ENTITY_TYPE, updated.getPermitId());

        log.info("Permit {} is now Active", permitId);
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse updateStatus(Long permitId, PermitStatus newStatus) {
        log.info("Updating permit {} status to {}", permitId, newStatus);
        WorkPermit permit = findOrThrow(permitId);
        enforceSiteAccess(permit);
        validateTransition(permit.getStatus(), newStatus);

        permit.setStatus(newStatus);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(currentUser.id(),
                "UPDATE_PERMIT_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int markExpiredPermits() {
        List<WorkPermit> expired =
                permitRepository.findByStatusAndEndDateTimeBefore(PermitStatus.ACTIVE, LocalDateTime.now());
        for (WorkPermit permit : expired) {
            permit.setStatus(PermitStatus.EXPIRED);
            permitRepository.save(permit);
            Long actorId = permit.getApprovedBy() != null
                    ? permit.getApprovedBy().getUserId()
                    : permit.getIssuedTo().getUserId();
            auditLogService.record(actorId, "PERMIT_EXPIRED", ENTITY_TYPE, permit.getPermitId());
        }
        log.info("Marked {} permits as Expired", expired.size());
        return expired.size();
    }

    @Override
    @Transactional(readOnly = true)
    public int remindExpiringPermits(int withinHours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusHours(withinHours);
        List<WorkPermit> soon = permitRepository
                .findByStatusAndEndDateTimeBetween(PermitStatus.ACTIVE, now, threshold);
        for (WorkPermit p : soon) {
            notificationRouter.notifyUser(p.getIssuedTo().getUserId(),
                    "Permit #" + p.getPermitId() + " expires at " + p.getEndDateTime(),
                    NotificationCategory.PERMIT);
        }
        log.info("Sent {} permit expiry warnings", soon.size());
        return soon.size();
    }

    // --- helpers ---

    void checkForConflicts(WorkPermit permit, Long excludePermitId) {
        List<WorkPermit> conflicts = permitRepository.findConflictingPermits(
                permit.getWorkLocation(), PermitStatus.ACTIVE,
                permit.getStartDateTime(), permit.getEndDateTime(), excludePermitId);
        if (!conflicts.isEmpty()) {
            WorkPermit c = conflicts.get(0);
            notificationRouter.notifyRoleAtSite(Role.PTW_COORDINATOR, permit.getSiteId(),
                    "Permit conflict detected at '" + permit.getWorkLocation()
                            + "' (conflicts with active permit #" + c.getPermitId() + ")",
                    NotificationCategory.PERMIT);
            throw new ConflictException(
                    "Permit conflicts with active permit " + c.getPermitId()
                            + " at location '" + permit.getWorkLocation() + "' during the requested time window");
        }
    }

    private void enforceSiteAccess(WorkPermit permit) {
        if (currentUser.hasAnyRole(Role.EHS_MANAGER, Role.COMPLIANCE_OFFICER, Role.ADMIN)) {
            return;
        }
        if (!permit.getSiteId().equals(currentUser.siteId())) {
            throw new AccessForbiddenException("You may only access permits for your assigned site");
        }
    }

    private void validateTransition(PermitStatus current, PermitStatus next) {
        boolean ok = switch (current) {
            case DRAFT -> next == PermitStatus.PENDING_APPROVAL;
            case PENDING_APPROVAL -> next == PermitStatus.ACTIVE || next == PermitStatus.DRAFT;
            case ACTIVE -> next == PermitStatus.SUSPENDED
                    || next == PermitStatus.CLOSED
                    || next == PermitStatus.EXPIRED;
            case SUSPENDED -> next == PermitStatus.ACTIVE || next == PermitStatus.CLOSED;
            case EXPIRED -> next == PermitStatus.CLOSED;
            case CLOSED -> false;
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid permit status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private WorkPermit findOrThrow(Long permitId) {
        return permitRepository.findById(permitId)
                .orElseThrow(() -> new ResourceNotFoundException("Permit not found with id: " + permitId));
    }
}