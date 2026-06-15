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
import com.cts.enums.NotificationCategory;
import com.cts.enums.Role;
import com.cts.exception.AccessForbiddenException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.InspectionMapper;
import com.cts.repository.InspectionScheduleRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.InspectionSpecification;
import com.cts.security.CurrentUser;
import com.cts.service.AuditLogService;
import com.cts.service.InspectionService;
import com.cts.service.NotificationRouter;

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
    private final CurrentUser currentUser;
    private final NotificationRouter notificationRouter;

    private static final String ENTITY_TYPE = "InspectionSchedule";

    @Override
    @Transactional
    public InspectionResponse scheduleInspection(InspectionRequest request) {
        log.info("Scheduling {} inspection at site {}", request.getInspectionType(), request.getSiteId());

        validateOfficer(request.getAssignedOfficerId());
        validatePlannedDate(request.getInspectionType(), request.getPlannedDate());

        InspectionSchedule schedule = inspectionMapper.toEntity(request);
        schedule.setStatus(InspectionStatus.SCHEDULED);

        InspectionSchedule saved = inspectionRepository.save(schedule);
        auditLogService.record(currentUser.id(), "CREATE_INSPECTION", ENTITY_TYPE, saved.getScheduleId());

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
            validatePlannedDate(request.getInspectionType(), date);

            InspectionSchedule schedule = InspectionSchedule.builder()
                    .siteId(request.getSiteId())
                    .inspectionType(request.getInspectionType())
                    .assignedOfficerId(request.getAssignedOfficerId())
                    .plannedDate(date)
                    .status(InspectionStatus.SCHEDULED)
                    .build();

            InspectionSchedule saved = inspectionRepository.save(schedule);
            auditLogService.record(currentUser.id(),
                    "CREATE_RECURRING_INSPECTION", ENTITY_TYPE, saved.getScheduleId());
            created.add(inspectionMapper.toResponse(saved));

            date = date.plusDays(request.getIntervalDays());
        }

        log.info("Created {} recurring inspections", created.size());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public InspectionResponse getInspectionById(Long scheduleId) {
        InspectionSchedule schedule = findOrThrow(scheduleId);
        enforceSiteAccess(schedule);
        return inspectionMapper.toResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspectionResponse> searchInspections(Long siteId, InspectionType inspectionType,
                                                      Long assignedOfficerId, InspectionStatus status,
                                                      LocalDate fromDate, LocalDate toDate) {
        Long effectiveSiteId = siteId;
        if (!currentUser.hasAnyRole(Role.EHS_MANAGER, Role.COMPLIANCE_OFFICER, Role.ADMIN)) {
            effectiveSiteId = currentUser.siteId();
        }
        var spec = InspectionSpecification.build(
                effectiveSiteId, inspectionType, assignedOfficerId, status, fromDate, toDate);
        return inspectionRepository.findAll(spec).stream()
                .map(inspectionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InspectionResponse updateStatus(Long scheduleId, InspectionStatus newStatus) {
        log.info("Updating inspection {} status to {}", scheduleId, newStatus);
        InspectionSchedule schedule = findOrThrow(scheduleId);
        enforceSiteAccess(schedule);
        validateTransition(schedule.getStatus(), newStatus);

        schedule.setStatus(newStatus);
        InspectionSchedule updated = inspectionRepository.save(schedule);
        auditLogService.record(currentUser.id(),
                "UPDATE_INSPECTION_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getScheduleId());
        return inspectionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int markMissedInspections() {
        List<InspectionSchedule> overdue =
                inspectionRepository.findByStatusAndPlannedDateBefore(InspectionStatus.SCHEDULED, LocalDate.now());
        for (InspectionSchedule schedule : overdue) {
            schedule.setStatus(InspectionStatus.MISSED);
            inspectionRepository.save(schedule);
            auditLogService.record(schedule.getAssignedOfficerId(),
                    "INSPECTION_MISSED", ENTITY_TYPE, schedule.getScheduleId());

            // Story 24: inspection missed -> notify the assigned Safety Officer and EHS Manager
            notificationRouter.notifyUser(schedule.getAssignedOfficerId(),
                    "Inspection #" + schedule.getScheduleId() + " at site " + schedule.getSiteId()
                            + " was MISSED (planned " + schedule.getPlannedDate() + ")",
                    NotificationCategory.INSPECTION);
            notificationRouter.notifyRole(Role.EHS_MANAGER,
                    "Inspection #" + schedule.getScheduleId() + " at site " + schedule.getSiteId() + " was MISSED",
                    NotificationCategory.INSPECTION);
        }
        log.info("Marked {} inspections as Missed", overdue.size());
        return overdue.size();
    }

    @Override
    @Transactional(readOnly = true)
    public int remindUpcomingInspections(int withinDays) {
        // Story 24: inspection approaching PlannedDate -> reminder (default ~24h / 1 day)
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(withinDays);
        List<InspectionSchedule> upcoming =
                inspectionRepository.findByStatusAndPlannedDateBetween(InspectionStatus.SCHEDULED, today, threshold);
        for (InspectionSchedule s : upcoming) {
            notificationRouter.notifyUser(s.getAssignedOfficerId(),
                    "Inspection #" + s.getScheduleId() + " is due on " + s.getPlannedDate(),
                    NotificationCategory.INSPECTION);
        }
        log.info("Sent {} inspection reminders", upcoming.size());
        return upcoming.size();
    }

    // --- private helpers ---

    private void enforceSiteAccess(InspectionSchedule schedule) {
        if (currentUser.hasAnyRole(Role.EHS_MANAGER, Role.COMPLIANCE_OFFICER, Role.ADMIN)) {
            return;
        }
        if (!schedule.getSiteId().equals(currentUser.siteId())) {
            throw new AccessForbiddenException("You may only access inspections for your assigned site");
        }
    }

    private void validateOfficer(Long officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer (User) not found with id: " + officerId));
        if (officer.getRole() != Role.SAFETY_OFFICER) {
            throw new IllegalArgumentException(
                    "AssignedOfficer must have role SafetyOfficer. User " + officerId
                            + " has role " + officer.getRole().getLabel());
        }
    }

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

    private void validateTransition(InspectionStatus current, InspectionStatus next) {
        boolean ok = switch (current) {
            case SCHEDULED -> next == InspectionStatus.COMPLETED
                    || next == InspectionStatus.MISSED
                    || next == InspectionStatus.RESCHEDULED;
            case RESCHEDULED -> next == InspectionStatus.COMPLETED
                    || next == InspectionStatus.MISSED
                    || next == InspectionStatus.SCHEDULED;
            case MISSED -> next == InspectionStatus.RESCHEDULED;
            case COMPLETED -> false;
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