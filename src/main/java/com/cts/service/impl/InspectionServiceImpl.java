package com.cts.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.InspectionRequest;
import com.cts.dto.request.RecurringInspectionRequest;
import com.cts.dto.response.InspectionResponse;
import com.cts.entity.InspectionSchedule;
import com.cts.entity.User;
import com.cts.enums.InspectionStatus;
import com.cts.enums.InspectionType;
import com.cts.enums.Role;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.InspectionMapper;
import com.cts.repository.InspectionScheduleRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.InspectionSpecification;
import com.cts.service.AuditLogService;
import com.cts.service.InspectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    private final InspectionScheduleRepository inspectionRepository;
    private final UserRepository userRepository;
    private final InspectionMapper inspectionMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "InspectionSchedule";

    @Override
    @Transactional
    public InspectionResponse scheduleInspection(InspectionRequest request) {
        log.info("Scheduling {} inspection at site {}", request.getInspectionType(), request.getSiteId());
        // RBAC: only Safety Officer may schedule (enforced in security step)

        // Story 17: assigned officer must be a valid User with Role = SafetyOfficer
        validateOfficer(request.getAssignedOfficerId());

        // Story 17: PlannedDate must be future, except IncidentFollow-Up (same-day allowed)
        validatePlannedDate(request.getInspectionType(), request.getPlannedDate());

        InspectionSchedule schedule = inspectionMapper.toEntity(request);
        schedule.setStatus(InspectionStatus.SCHEDULED); // lifecycle start

        InspectionSchedule saved = inspectionRepository.save(schedule);
        auditLogService.record(saved.getAssignedOfficerId(), "CREATE_INSPECTION", ENTITY_TYPE, saved.getScheduleId());

        // NOTIFY: inspection approaching PlannedDate -> reminder (Notification module, Story 24)

        log.info("Inspection scheduled with id: {}", saved.getScheduleId());
        return inspectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<InspectionResponse> scheduleRecurring(RecurringInspectionRequest request) {
        log.info("Scheduling {} recurring {} inspections at site {}",
                request.getOccurrences(), request.getInspectionType(), request.getSiteId());

        validateOfficer(request.getAssignedOfficerId());

        List<InspectionResponse> created = new ArrayList<>();
        LocalDate date = request.getStartDate();

        for (int i = 0; i < request.getOccurrences(); i++) {
            // Each generated occurrence must still satisfy the date rule
            validatePlannedDate(request.getInspectionType(), date);

            InspectionSchedule schedule = InspectionSchedule.builder()
                    .siteId(request.getSiteId())
                    .inspectionType(request.getInspectionType())
                    .assignedOfficerId(request.getAssignedOfficerId())
                    .plannedDate(date)
                    .status(InspectionStatus.SCHEDULED)
                    .build();

            InspectionSchedule saved = inspectionRepository.save(schedule);
            auditLogService.record(saved.getAssignedOfficerId(),
                    "CREATE_RECURRING_INSPECTION", ENTITY_TYPE, saved.getScheduleId());
            created.add(inspectionMapper.toResponse(saved));

            date = date.plusDays(request.getIntervalDays()); // next occurrence
        }

        log.info("Created {} recurring inspections", created.size());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public InspectionResponse getInspectionById(Long scheduleId) {
        return inspectionMapper.toResponse(findOrThrow(scheduleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspectionResponse> searchInspections(Long siteId, InspectionType inspectionType,
                                                      Long assignedOfficerId, InspectionStatus status,
                                                      LocalDate fromDate, LocalDate toDate) {
        var spec = InspectionSpecification.build(siteId, inspectionType, assignedOfficerId, status, fromDate, toDate);
        return inspectionRepository.findAll(spec).stream()
                .map(inspectionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InspectionResponse updateStatus(Long scheduleId, InspectionStatus newStatus) {
        log.info("Updating inspection {} status to {}", scheduleId, newStatus);
        InspectionSchedule schedule = findOrThrow(scheduleId);
        validateTransition(schedule.getStatus(), newStatus);

        schedule.setStatus(newStatus);
        InspectionSchedule updated = inspectionRepository.save(schedule);
        auditLogService.record(updated.getAssignedOfficerId(),
                "UPDATE_INSPECTION_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getScheduleId());
        return inspectionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int markMissedInspections() {
        // Story 17: auto-transition to Missed if PlannedDate has passed and still Scheduled
        List<InspectionSchedule> overdue =
                inspectionRepository.findByStatusAndPlannedDateBefore(InspectionStatus.SCHEDULED, LocalDate.now());
        for (InspectionSchedule schedule : overdue) {
            schedule.setStatus(InspectionStatus.MISSED);
            inspectionRepository.save(schedule);
            auditLogService.record(schedule.getAssignedOfficerId(),
                    "INSPECTION_MISSED", ENTITY_TYPE, schedule.getScheduleId());
            // NOTIFY: inspection missed -> notify Safety Officer and EHS Manager (Story 24)
        }
        log.info("Marked {} inspections as Missed", overdue.size());
        return overdue.size();
    }

    // --- private helpers ---

    // Story 17: AssignedOfficerID must reference a User with Role = SafetyOfficer
    private void validateOfficer(Long officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer (User) not found with id: " + officerId));
        if (officer.getRole() != Role.SAFETY_OFFICER) {
            throw new IllegalArgumentException(
                    "AssignedOfficer must have role SafetyOfficer. User " + officerId
                            + " has role " + officer.getRole().getLabel());
        }
    }

    // Story 17: future date required, except IncidentFollow-Up which may be same-day
    private void validatePlannedDate(InspectionType type, LocalDate plannedDate) {
        LocalDate today = LocalDate.now();
        if (type == InspectionType.INCIDENT_FOLLOW_UP) {
            if (plannedDate.isBefore(today)) {
                throw new IllegalArgumentException("PlannedDate cannot be in the past");
            }
        } else {
            if (!plannedDate.isAfter(today)) {
                throw new IllegalArgumentException(
                        "PlannedDate must be a future date for " + type.getLabel() + " inspections");
            }
        }
    }

    // Story 17 lifecycle: Scheduled -> Completed or Missed, plus Rescheduled
    private void validateTransition(InspectionStatus current, InspectionStatus next) {
        boolean ok = switch (current) {
            case SCHEDULED -> next == InspectionStatus.COMPLETED
                    || next == InspectionStatus.MISSED
                    || next == InspectionStatus.RESCHEDULED;
            case RESCHEDULED -> next == InspectionStatus.COMPLETED
                    || next == InspectionStatus.MISSED
                    || next == InspectionStatus.SCHEDULED;
            case MISSED -> next == InspectionStatus.RESCHEDULED;
            case COMPLETED -> false; // terminal
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid inspection status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private InspectionSchedule findOrThrow(Long scheduleId) {
        return inspectionRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found with id: " + scheduleId));
    }
}