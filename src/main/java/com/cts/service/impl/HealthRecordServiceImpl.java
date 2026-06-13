package com.cts.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.HealthRecordRequest;
import com.cts.dto.response.HealthRecordResponse;
import com.cts.entity.HealthRecord;
import com.cts.entity.User;
import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HealthRecordStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.Role;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.HealthRecordMapper;
import com.cts.repository.HealthRecordRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.HealthRecordSpecification;
import com.cts.service.AuditLogService;
import com.cts.service.HealthRecordService;
import com.cts.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final UserRepository userRepository;
    private final HealthRecordMapper healthRecordMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    private static final String ENTITY_TYPE = "HealthRecord";

    @Override
    @Transactional
    public HealthRecordResponse createHealthRecord(HealthRecordRequest request) {
        log.info("Creating {} health record for employee {}",
                request.getAssessmentType(), request.getEmployeeId());
        // PII: read access restricted to OHNurse/EHSManager/self - enforced in security step

        // EmployeeID must be a valid user
        if (!userRepository.existsById(request.getEmployeeId())) {
            throw new ResourceNotFoundException("Employee (User) not found with id: " + request.getEmployeeId());
        }

        // Story 21: ConductedByID must be a User with Role = OHNurse
        User conductor = userRepository.findById(request.getConductedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor (User) not found with id: " + request.getConductedById()));
        if (conductor.getRole() != Role.OH_NURSE) {
            throw new IllegalArgumentException(
                    "ConductedBy must have role OHNurse. User " + request.getConductedById()
                            + " has role " + conductor.getRole().getLabel());
        }

        // Story 21: NextAssessmentDate required for Periodic and PostIncident, and must be future
        boolean requiresNext = request.getAssessmentType() == AssessmentType.PERIODIC
                || request.getAssessmentType() == AssessmentType.POST_INCIDENT;
        if (requiresNext && request.getNextAssessmentDate() == null) {
            throw new IllegalArgumentException(
                    "NextAssessmentDate is required for " + request.getAssessmentType().getLabel() + " assessments");
        }
        if (request.getNextAssessmentDate() != null && !request.getNextAssessmentDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("NextAssessmentDate must be a future date");
        }

        HealthRecord record = healthRecordMapper.toEntity(request);
        record.setStatus(HealthRecordStatus.COMPLETED); // default; can be set to PendingReview later

        HealthRecord saved = healthRecordRepository.save(record);
        auditLogService.record(saved.getConductedById(), "CREATE_HEALTH_RECORD", ENTITY_TYPE, saved.getHealthRecordId());

        // Story 21: flag restricted duty
        if (saved.getFitnessDecision() == FitnessDecision.TEMPORARY_UNFIT
                || saved.getFitnessDecision() == FitnessDecision.PERMANENTLY_UNFIT) {
            log.info("Employee {} flagged for restricted duty ({})",
                    saved.getEmployeeId(), saved.getFitnessDecision().getLabel());
        }

        log.info("Health record created with id: {}", saved.getHealthRecordId());
        return healthRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthRecordResponse getHealthRecordById(Long healthRecordId) {
        // PII: enforce OHNurse/EHSManager/self access in security step
        return healthRecordMapper.toResponse(findOrThrow(healthRecordId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthRecordResponse> searchHealthRecords(Long employeeId, AssessmentType assessmentType,
                                                          FitnessDecision fitnessDecision, HealthRecordStatus status,
                                                          Long conductedById,
                                                          LocalDate assessmentFrom, LocalDate assessmentTo,
                                                          LocalDate nextFrom, LocalDate nextTo) {
        var spec = HealthRecordSpecification.build(employeeId, assessmentType, fitnessDecision, status,
                conductedById, assessmentFrom, assessmentTo, nextFrom, nextTo);
        return healthRecordRepository.findAll(spec).stream()
                .map(healthRecordMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HealthRecordResponse updateStatus(Long healthRecordId, HealthRecordStatus status) {
        HealthRecord record = findOrThrow(healthRecordId);
        record.setStatus(status);
        HealthRecord updated = healthRecordRepository.save(record);
        auditLogService.record(updated.getConductedById(),
                "UPDATE_HEALTH_RECORD_STATUS_" + status.name(), ENTITY_TYPE, updated.getHealthRecordId());
        return healthRecordMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int remindUpcomingAssessments(int withinDays) {
        // Story 21: surveillance approaching NextAssessmentDate -> Notification (Category = Health)
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(withinDays);

        // find records whose next assessment falls within [today, threshold]
        var spec = HealthRecordSpecification.build(null, null, null, null, null, null, null, today, threshold);
        List<HealthRecord> due = healthRecordRepository.findAll(spec);

        for (HealthRecord record : due) {
            notificationService.create(
                    record.getConductedById(),
                    "Health surveillance for employee " + record.getEmployeeId()
                            + " is due on " + record.getNextAssessmentDate(),
                    NotificationCategory.HEALTH);
        }
        log.info("Sent {} health surveillance reminders", due.size());
        return due.size();
    }

    private HealthRecord findOrThrow(Long healthRecordId) {
        return healthRecordRepository.findById(healthRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Health record not found with id: " + healthRecordId));
    }
}