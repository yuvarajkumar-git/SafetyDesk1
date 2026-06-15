package com.cts.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.ExtensionDecisionRequest;
import com.cts.dto.request.ExtensionRequest;
import com.cts.dto.response.ExtensionResponse;
import com.cts.entity.PermitExtension;
import com.cts.entity.User;
import com.cts.entity.WorkPermit;
import com.cts.enums.ExtensionStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.PermitStatus;
import com.cts.enums.Role;
import com.cts.exception.ConflictException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.ExtensionMapper;
import com.cts.repository.PermitExtensionRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.WorkPermitRepository;
import com.cts.service.AuditLogService;
import com.cts.service.ExtensionService;
import com.cts.service.NotificationRouter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtensionServiceImpl implements ExtensionService {

    private final PermitExtensionRepository extensionRepository;
    private final WorkPermitRepository permitRepository;
    private final UserRepository userRepository;
    private final ExtensionMapper extensionMapper;
    private final AuditLogService auditLogService;
    private final NotificationRouter notificationRouter;

    private static final String ENTITY_TYPE = "PermitExtension";

    @Override
    @Transactional
    public ExtensionResponse requestExtension(ExtensionRequest request) {
        log.info("Requesting extension for permit {}", request.getPermitId());

        // Story 20: PermitID must reference an Active permit
        WorkPermit permit = permitRepository.findById(request.getPermitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Permit not found with id: " + request.getPermitId()));
        if (permit.getStatus() != PermitStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Extensions can only be requested for an Active permit. Current status: "
                            + permit.getStatus().getLabel());
        }

        if (!userRepository.existsById(request.getRequestedById())) {
            throw new ResourceNotFoundException(
                    "Requester (User) not found with id: " + request.getRequestedById());
        }

        if (!request.getNewEndDateTime().isAfter(permit.getEndDateTime())) {
            throw new IllegalArgumentException(
                    "NewEndDateTime must be later than the permit's current EndDateTime");
        }

        PermitExtension ext = extensionMapper.toEntity(request);
        ext.setStatus(ExtensionStatus.REQUESTED);

        PermitExtension saved = extensionRepository.save(ext);
        auditLogService.record(saved.getRequestedById(), "REQUEST_EXTENSION", ENTITY_TYPE, saved.getExtensionId());

        // Story 24: extension request submitted -> notify approver pool (PTW Coordinators at the permit's site)
        notificationRouter.notifyRoleAtSite(Role.PTW_COORDINATOR, permit.getSiteId(),
                "Permit extension requested for permit #" + permit.getPermitId()
                        + " (new end " + saved.getNewEndDateTime() + ")",
                NotificationCategory.PERMIT);

        log.info("Extension requested with id: {}", saved.getExtensionId());
        return extensionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExtensionResponse getExtensionById(Long extensionId) {
        return extensionMapper.toResponse(findOrThrow(extensionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExtensionResponse> getByPermitId(Long permitId) {
        return extensionRepository.findByPermitId(permitId).stream()
                .map(extensionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExtensionResponse> getByStatus(ExtensionStatus status) {
        return extensionRepository.findByStatus(status).stream()
                .map(extensionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExtensionResponse approveExtension(Long extensionId, ExtensionDecisionRequest request) {
        log.info("Approving extension {}", extensionId);
        PermitExtension ext = findOrThrow(extensionId);
        if (ext.getStatus() != ExtensionStatus.REQUESTED) {
            throw new IllegalArgumentException(
                    "Only a Requested extension can be approved. Current status: " + ext.getStatus().getLabel());
        }

        User approver = userRepository.findById(request.getApprovedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Approver (User) not found with id: " + request.getApprovedById()));
        if (request.getApprovedById().equals(ext.getRequestedById())) {
            throw new IllegalArgumentException(
                    "ApprovedByID must differ from RequestedByID (separation of duties)");
        }
        if (approver.getRole() != Role.PTW_COORDINATOR && approver.getRole() != Role.SAFETY_OFFICER) {
            throw new IllegalArgumentException(
                    "Extension approver must be a PTWCoordinator or SafetyOfficer. User "
                            + request.getApprovedById() + " has role " + approver.getRole().getLabel());
        }

        WorkPermit permit = permitRepository.findById(ext.getPermitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Permit not found with id: " + ext.getPermitId()));

        // Story 20: re-run conflict detection for the EXTENDED window, excluding this permit itself
        List<WorkPermit> conflicts = permitRepository.findConflictingPermits(
                permit.getWorkLocation(), PermitStatus.ACTIVE,
                permit.getStartDateTime(), ext.getNewEndDateTime(), permit.getPermitId());
        if (!conflicts.isEmpty()) {
            // Story 24: conflict during extension -> notify PTW Coordinators
            notificationRouter.notifyRoleAtSite(Role.PTW_COORDINATOR, permit.getSiteId(),
                    "Extension conflict for permit #" + permit.getPermitId()
                            + " (conflicts with active permit #" + conflicts.get(0).getPermitId() + ")",
                    NotificationCategory.PERMIT);
            throw new ConflictException(
                    "Extended time window conflicts with active permit " + conflicts.get(0).getPermitId()
                            + " at location '" + permit.getWorkLocation() + "'");
        }

        ext.setApprovedById(request.getApprovedById());
        ext.setStatus(ExtensionStatus.APPROVED);
        PermitExtension savedExt = extensionRepository.save(ext);

        permit.setEndDateTime(ext.getNewEndDateTime());
        permitRepository.save(permit);

        auditLogService.record(request.getApprovedById(), "APPROVE_EXTENSION", ENTITY_TYPE, savedExt.getExtensionId());
        auditLogService.record(request.getApprovedById(), "UPDATE_PERMIT_END_TIME", "WorkPermit", permit.getPermitId());

        log.info("Extension {} approved; permit {} end time updated", extensionId, permit.getPermitId());
        return extensionMapper.toResponse(savedExt);
    }

    @Override
    @Transactional
    public ExtensionResponse rejectExtension(Long extensionId, ExtensionDecisionRequest request) {
        log.info("Rejecting extension {}", extensionId);
        PermitExtension ext = findOrThrow(extensionId);
        if (ext.getStatus() != ExtensionStatus.REQUESTED) {
            throw new IllegalArgumentException(
                    "Only a Requested extension can be rejected. Current status: " + ext.getStatus().getLabel());
        }

        if (!userRepository.existsById(request.getApprovedById())) {
            throw new ResourceNotFoundException(
                    "Approver (User) not found with id: " + request.getApprovedById());
        }

        ext.setApprovedById(request.getApprovedById());
        ext.setStatus(ExtensionStatus.REJECTED);
        PermitExtension saved = extensionRepository.save(ext);
        auditLogService.record(request.getApprovedById(), "REJECT_EXTENSION", ENTITY_TYPE, saved.getExtensionId());
        return extensionMapper.toResponse(saved);
    }

    private PermitExtension findOrThrow(Long extensionId) {
        return extensionRepository.findById(extensionId)
                .orElseThrow(() -> new ResourceNotFoundException("Extension not found with id: " + extensionId));
    }
}