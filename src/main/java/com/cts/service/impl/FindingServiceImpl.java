package com.cts.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.FindingBatchRequest;
import com.cts.dto.request.FindingRequest;
import com.cts.dto.response.FindingResponse;
import com.cts.entity.InspectionFinding;
import com.cts.entity.InspectionSchedule;
import com.cts.enums.FindingStatus;
import com.cts.enums.FindingType;
import com.cts.enums.InspectionStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.RiskLevel;
import com.cts.enums.Role;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.FindingMapper;
import com.cts.repository.InspectionFindingRepository;
import com.cts.repository.InspectionScheduleRepository;
import com.cts.repository.spec.FindingSpecification;
import com.cts.service.AuditLogService;
import com.cts.service.FindingService;
import com.cts.service.NotificationRouter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindingServiceImpl implements FindingService {

    private final InspectionFindingRepository findingRepository;
    private final InspectionScheduleRepository inspectionRepository;
    private final FindingMapper findingMapper;
    private final AuditLogService auditLogService;
    private final NotificationRouter notificationRouter;

    private static final String ENTITY_TYPE = "InspectionFinding";

    @Override
    @Transactional
    public FindingResponse createFinding(FindingRequest request) {
        return findingMapper.toResponse(persistNewFinding(request));
    }

    @Override
    @Transactional
    public List<FindingResponse> createFindingsBatch(FindingBatchRequest request) {
        log.info("Batch-creating {} findings", request.getFindings().size());
        List<FindingResponse> created = new ArrayList<>();
        for (FindingRequest item : request.getFindings()) {
            created.add(findingMapper.toResponse(persistNewFinding(item)));
        }
        return created;
    }

    private InspectionFinding persistNewFinding(FindingRequest request) {
        log.info("Recording finding for schedule {}", request.getScheduleId());

        // Need the schedule object to validate its status; mapper will also resolve it
        InspectionSchedule schedule = inspectionRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inspection not found with id: " + request.getScheduleId()));
        if (schedule.getStatus() != InspectionStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Findings can only be recorded against a Completed inspection. Current status: "
                            + schedule.getStatus().getLabel());
        }

        if (request.getFindingType() == FindingType.NON_CONFORMANCE && request.getDueDate() == null) {
            throw new IllegalArgumentException("DueDate is required for NonConformance findings");
        }

        // Assignee existence is validated inside the mapper (loads or throws)
        InspectionFinding finding = findingMapper.toEntity(request);
        finding.setStatus(FindingStatus.OPEN);
        InspectionFinding saved = findingRepository.save(finding);
        auditLogService.record(saved.getAssignedTo().getUserId(), "CREATE_FINDING", ENTITY_TYPE, saved.getFindingId());

        // Story 24: RiskLevel = Critical -> immediate escalation to EHS Manager
        if (saved.getRiskLevel() == RiskLevel.CRITICAL) {
            log.warn("CRITICAL finding {} raised - escalation required", saved.getFindingId());
            notificationRouter.notifyRole(Role.EHS_MANAGER,
                    "CRITICAL finding #" + saved.getFindingId() + " raised at " + saved.getLocation()
                            + " (schedule " + saved.getSchedule().getScheduleId() + ")",
                    NotificationCategory.INSPECTION);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public FindingResponse getFindingById(Long findingId) {
        return findingMapper.toResponse(findOrThrow(findingId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindingResponse> searchFindings(Long scheduleId, FindingType findingType, RiskLevel riskLevel,
                                                FindingStatus status, Long assignedToId,
                                                LocalDate fromDate, LocalDate toDate) {
        var spec = FindingSpecification.build(scheduleId, findingType, riskLevel, status, assignedToId, fromDate, toDate);
        return findingRepository.findAll(spec).stream()
                .map(findingMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FindingResponse updateStatus(Long findingId, FindingStatus newStatus) {
        log.info("Updating finding {} status to {}", findingId, newStatus);
        InspectionFinding finding = findOrThrow(findingId);
        validateTransition(finding.getStatus(), newStatus);

        finding.setStatus(newStatus);
        InspectionFinding updated = findingRepository.save(finding);
        auditLogService.record(updated.getAssignedTo().getUserId(),
                "UPDATE_FINDING_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getFindingId());
        return findingMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int markOverdueFindings() {
        List<InspectionFinding> overdue = findingRepository.findByDueDateBeforeAndStatusNotIn(
                LocalDate.now(), List.of(FindingStatus.CLOSED, FindingStatus.OVERDUE));
        for (InspectionFinding finding : overdue) {
            if (finding.getDueDate() == null) {
                continue;
            }
            finding.setStatus(FindingStatus.OVERDUE);
            findingRepository.save(finding);
            Long assigneeId = finding.getAssignedTo().getUserId();
            auditLogService.record(assigneeId, "FINDING_OVERDUE", ENTITY_TYPE, finding.getFindingId());

            // Story 24: overdue finding -> Inspection notification to the assignee
            notificationRouter.notifyUser(assigneeId,
                    "Finding #" + finding.getFindingId() + " is OVERDUE (due " + finding.getDueDate() + ")",
                    NotificationCategory.INSPECTION);
        }
        log.info("Marked {} findings as Overdue", overdue.size());
        return overdue.size();
    }

    private void validateTransition(FindingStatus current, FindingStatus next) {
        boolean ok = switch (current) {
            case OPEN -> next == FindingStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == FindingStatus.CLOSED;
            case OVERDUE -> next == FindingStatus.IN_PROGRESS || next == FindingStatus.CLOSED;
            case CLOSED -> false;
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid finding status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private InspectionFinding findOrThrow(Long findingId) {
        return findingRepository.findById(findingId)
                .orElseThrow(() -> new ResourceNotFoundException("Finding not found with id: " + findingId));
    }
}