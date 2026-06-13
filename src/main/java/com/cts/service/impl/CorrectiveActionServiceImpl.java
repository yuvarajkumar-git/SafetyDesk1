package com.cts.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.CorrectiveActionBatchRequest;
import com.cts.dto.request.CorrectiveActionRequest;
import com.cts.dto.response.CorrectiveActionResponse;
import com.cts.entity.CorrectiveAction;
import com.cts.enums.CorrectiveActionStatus;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.CorrectiveActionMapper;
import com.cts.repository.CorrectiveActionRepository;
import com.cts.repository.IncidentReportRepository;
import com.cts.repository.UserRepository;
import com.cts.service.AuditLogService;
import com.cts.service.CorrectiveActionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorrectiveActionServiceImpl implements CorrectiveActionService {

    private final CorrectiveActionRepository actionRepository;
    private final IncidentReportRepository incidentRepository;
    private final UserRepository userRepository;
    private final CorrectiveActionMapper actionMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "CorrectiveAction";

    @Override
    @Transactional
    public CorrectiveActionResponse createAction(CorrectiveActionRequest request) {
        log.info("Creating corrective action for incident: {}", request.getIncidentId());
        CorrectiveAction saved = persistNewAction(request);
        return actionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<CorrectiveActionResponse> createActionsBatch(CorrectiveActionBatchRequest request) {
        log.info("Batch-creating {} corrective actions", request.getActions().size());
        List<CorrectiveActionResponse> created = new ArrayList<>();
        for (CorrectiveActionRequest item : request.getActions()) {
            created.add(actionMapper.toResponse(persistNewAction(item)));
        }
        return created;
    }

    // Shared creation logic used by both single and batch create
    private CorrectiveAction persistNewAction(CorrectiveActionRequest request) {
        // IncidentID must reference a valid incident
        if (!incidentRepository.existsById(request.getIncidentId())) {
            throw new ResourceNotFoundException("Incident not found with id: " + request.getIncidentId());
        }
        // AssignedToID must reference a valid user
        if (!userRepository.existsById(request.getAssignedToId())) {
            throw new ResourceNotFoundException("Assignee (User) not found with id: " + request.getAssignedToId());
        }

        CorrectiveAction action = actionMapper.toEntity(request);
        action.setStatus(CorrectiveActionStatus.OPEN); // lifecycle start
        CorrectiveAction saved = actionRepository.save(action);
        auditLogService.record(saved.getAssignedToId(), "CREATE_CORRECTIVE_ACTION", ENTITY_TYPE, saved.getActionId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public CorrectiveActionResponse getActionById(Long actionId) {
        return actionMapper.toResponse(findOrThrow(actionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrectiveActionResponse> getByIncidentId(Long incidentId) {
        return actionRepository.findByIncidentId(incidentId).stream()
                .map(actionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrectiveActionResponse> getByStatus(CorrectiveActionStatus status) {
        return actionRepository.findByStatus(status).stream()
                .map(actionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CorrectiveActionResponse updateStatus(Long actionId, CorrectiveActionStatus newStatus, Long verifiedById) {
        log.info("Updating corrective action {} to {}", actionId, newStatus);
        CorrectiveAction action = findOrThrow(actionId);
        validateTransition(action.getStatus(), newStatus);

        if (newStatus == CorrectiveActionStatus.COMPLETED) {
            // Story 14: ClosedDate auto-populated on Completed
            action.setClosedDate(LocalDate.now());
        }

        if (newStatus == CorrectiveActionStatus.VERIFIED) {
            // RBAC: only Safety Officer / EHS Manager may verify (enforced in security step)
            if (verifiedById == null) {
                throw new IllegalArgumentException("VerifiedByID is required to verify a corrective action");
            }
            if (!userRepository.existsById(verifiedById)) {
                throw new ResourceNotFoundException("Verifier (User) not found with id: " + verifiedById);
            }
            // Story 14: separation of duties - verifier must differ from assignee
            if (verifiedById.equals(action.getAssignedToId())) {
                throw new IllegalArgumentException(
                        "VerifiedByID must be different from AssignedToID (separation of duties)");
            }
            action.setVerifiedById(verifiedById);
        }

        action.setStatus(newStatus);
        CorrectiveAction updated = actionRepository.save(action);
        auditLogService.record(updated.getAssignedToId(),
                "UPDATE_CORRECTIVE_ACTION_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getActionId());
        return actionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int markOverdueActions() {
        // Story 14: auto-set Overdue when DueDate passed and not Completed/Verified
        List<CorrectiveAction> overdue = actionRepository.findByDueDateBeforeAndStatusNotIn(
                LocalDate.now(),
                List.of(CorrectiveActionStatus.COMPLETED, CorrectiveActionStatus.VERIFIED,
                        CorrectiveActionStatus.OVERDUE));
        for (CorrectiveAction action : overdue) {
            action.setStatus(CorrectiveActionStatus.OVERDUE);
            actionRepository.save(action);
            auditLogService.record(action.getAssignedToId(),
                    "CORRECTIVE_ACTION_OVERDUE", ENTITY_TYPE, action.getActionId());
            // Notification (Category = CAPA) deferred to Notification module (Story 24)
        }
        log.info("Marked {} corrective actions as Overdue", overdue.size());
        return overdue.size();
    }

    // Story 14 lifecycle: Open -> InProgress -> Completed -> Verified (+ Overdue handled separately)
    private void validateTransition(CorrectiveActionStatus current, CorrectiveActionStatus next) {
        boolean ok = switch (current) {
            case OPEN -> next == CorrectiveActionStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == CorrectiveActionStatus.COMPLETED;
            case COMPLETED -> next == CorrectiveActionStatus.VERIFIED;
            case OVERDUE -> next == CorrectiveActionStatus.IN_PROGRESS || next == CorrectiveActionStatus.COMPLETED;
            case VERIFIED -> false; // terminal
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid corrective action transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private CorrectiveAction findOrThrow(Long actionId) {
        return actionRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Corrective action not found with id: " + actionId));
    }
}