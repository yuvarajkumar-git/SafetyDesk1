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
import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;
import com.cts.enums.Role;
import com.cts.exception.ConflictException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.PermitMapper;
import com.cts.repository.UserRepository;
import com.cts.repository.WorkPermitRepository;
import com.cts.repository.spec.PermitSpecification;
import com.cts.service.AuditLogService;
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

    private static final String ENTITY_TYPE = "WorkPermit";
    // Used when checking conflicts for a brand-new permit (no id to exclude)
    private static final Long NO_EXCLUSION = -1L;

    @Override
    @Transactional
    public PermitResponse createPermit(PermitRequest request) {
        log.info("Creating {} permit at {}", request.getPermitType(), request.getWorkLocation());

        // IssuedToID must reference a valid user
        if (!userRepository.existsById(request.getIssuedToId())) {
            throw new ResourceNotFoundException("IssuedTo (User) not found with id: " + request.getIssuedToId());
        }
        // EndDateTime must be after StartDateTime
        if (!request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new IllegalArgumentException("EndDateTime must be after StartDateTime");
        }

        WorkPermit permit = permitMapper.toEntity(request);
        permit.setStatus(PermitStatus.DRAFT); // lifecycle start

        WorkPermit saved = permitRepository.save(permit);
        auditLogService.record(saved.getIssuedToId(), "CREATE_PERMIT", ENTITY_TYPE, saved.getPermitId());

        log.info("Permit created in Draft with id: {}", saved.getPermitId());
        return permitMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PermitResponse getPermitById(Long permitId) {
        return permitMapper.toResponse(findOrThrow(permitId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermitResponse> searchPermits(Long siteId, PermitType permitType, PermitStatus status,
                                              String workLocation, Long issuedToId, Long approvedById,
                                              LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        var spec = PermitSpecification.build(
                siteId, permitType, status, workLocation, issuedToId, approvedById, fromDateTime, toDateTime);
        return permitRepository.findAll(spec).stream()
                .map(permitMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PermitResponse updatePermit(Long permitId, PermitUpdateRequest request) {
        WorkPermit permit = findOrThrow(permitId);
        // Only a Draft permit can be edited
        if (permit.getStatus() != PermitStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only a Draft permit can be edited. Current status: " + permit.getStatus().getLabel());
        }
        if (request.getWorkDescription() != null)  permit.setWorkDescription(request.getWorkDescription());
        if (request.getHazardsIdentified() != null) permit.setHazardsIdentified(request.getHazardsIdentified());
        if (request.getControlMeasures() != null)  permit.setControlMeasures(request.getControlMeasures());

        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(updated.getIssuedToId(), "UPDATE_PERMIT", ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse submitForApproval(Long permitId) {
        log.info("Submitting permit {} for approval", permitId);
        WorkPermit permit = findOrThrow(permitId);
        validateTransition(permit.getStatus(), PermitStatus.PENDING_APPROVAL);

        // Story 19: HazardsIdentified and ControlMeasures mandatory before PendingApproval
        if (isBlank(permit.getHazardsIdentified()) || isBlank(permit.getControlMeasures())) {
            throw new IllegalArgumentException(
                    "HazardsIdentified and ControlMeasures must be completed before submitting for approval");
        }

        permit.setStatus(PermitStatus.PENDING_APPROVAL);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(updated.getIssuedToId(), "SUBMIT_PERMIT", ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse approvePermit(Long permitId, PermitApprovalRequest request) {
        log.info("Approving permit {} by user {}", permitId, request.getApprovedById());
        // RBAC: caller authorization enforced in security step

        WorkPermit permit = findOrThrow(permitId);
        if (permit.getStatus() != PermitStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException(
                    "Only a PendingApproval permit can be approved. Current status: " + permit.getStatus().getLabel());
        }

        // Story 19: approval requires at least a PTW Coordinator (multi-level workflow simplified)
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

        permit.setApprovedById(request.getApprovedById());
        // Approval keeps it PendingApproval until explicitly activated, OR we can move straight to Active.
        // Per lifecycle Draft->PendingApproval->Active->Closed, we activate in a separate step.
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(request.getApprovedById(), "APPROVE_PERMIT", ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse activatePermit(Long permitId) {
        log.info("Activating permit {}", permitId);
        WorkPermit permit = findOrThrow(permitId);
        validateTransition(permit.getStatus(), PermitStatus.ACTIVE);

        // Must have been approved
        if (permit.getApprovedById() == null) {
            throw new IllegalArgumentException("Permit must be approved before activation");
        }

        // Story 19: conflict detection - no overlapping Active permit at same location
        checkForConflicts(permit, NO_EXCLUSION);

        permit.setStatus(PermitStatus.ACTIVE);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(updated.getApprovedById(), "ACTIVATE_PERMIT", ENTITY_TYPE, updated.getPermitId());

        // NOTIFY: permit approaching EndDateTime -> expiry warning (Story 24)

        log.info("Permit {} is now Active", permitId);
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PermitResponse updateStatus(Long permitId, PermitStatus newStatus) {
        log.info("Updating permit {} status to {}", permitId, newStatus);
        WorkPermit permit = findOrThrow(permitId);
        validateTransition(permit.getStatus(), newStatus);

        permit.setStatus(newStatus);
        WorkPermit updated = permitRepository.save(permit);
        auditLogService.record(
                updated.getApprovedById() != null ? updated.getApprovedById() : updated.getIssuedToId(),
                "UPDATE_PERMIT_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getPermitId());
        return permitMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int markExpiredPermits() {
        // Story 19: auto-Expired when EndDateTime has passed and status is Active
        List<WorkPermit> expired =
                permitRepository.findByStatusAndEndDateTimeBefore(PermitStatus.ACTIVE, LocalDateTime.now());
        for (WorkPermit permit : expired) {
            permit.setStatus(PermitStatus.EXPIRED);
            permitRepository.save(permit);
            auditLogService.record(
                    permit.getApprovedById() != null ? permit.getApprovedById() : permit.getIssuedToId(),
                    "PERMIT_EXPIRED", ENTITY_TYPE, permit.getPermitId());
        }
        log.info("Marked {} permits as Expired", expired.size());
        return expired.size();
    }

    // --- helpers ---

    // Shared conflict check, reused by activation AND extension (Story 19/20).
    // excludePermitId lets the extension ignore the permit being extended.
    void checkForConflicts(WorkPermit permit, Long excludePermitId) {
        List<WorkPermit> conflicts = permitRepository.findConflictingPermits(
                permit.getWorkLocation(), PermitStatus.ACTIVE,
                permit.getStartDateTime(), permit.getEndDateTime(), excludePermitId);
        if (!conflicts.isEmpty()) {
            WorkPermit c = conflicts.get(0);
            // NOTIFY: permit conflict detected -> notify PTW Coordinator (Story 24)
            throw new ConflictException(
                    "Permit conflicts with active permit " + c.getPermitId()
                            + " at location '" + permit.getWorkLocation() + "' during the requested time window");
        }
    }

    // Story 19 lifecycle: Draft -> PendingApproval -> Active -> Closed; plus Suspended, Expired
    private void validateTransition(PermitStatus current, PermitStatus next) {
        boolean ok = switch (current) {
            case DRAFT -> next == PermitStatus.PENDING_APPROVAL;
            case PENDING_APPROVAL -> next == PermitStatus.ACTIVE || next == PermitStatus.DRAFT;
            case ACTIVE -> next == PermitStatus.SUSPENDED
                    || next == PermitStatus.CLOSED
                    || next == PermitStatus.EXPIRED;
            case SUSPENDED -> next == PermitStatus.ACTIVE || next == PermitStatus.CLOSED;
            case EXPIRED -> next == PermitStatus.CLOSED;
            case CLOSED -> false; // terminal
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