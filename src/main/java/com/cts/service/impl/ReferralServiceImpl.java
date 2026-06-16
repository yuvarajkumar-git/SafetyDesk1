package com.cts.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.ReferralOutcomeRequest;
import com.cts.dto.request.ReferralRequest;
import com.cts.dto.response.ReferralResponse;
import com.cts.entity.HealthRecord;
import com.cts.entity.MedicalReferral;
import com.cts.enums.NotificationCategory;
import com.cts.enums.ReferralStatus;
import com.cts.enums.Role;
import com.cts.exception.AccessForbiddenException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.ReferralMapper;
import com.cts.repository.HealthRecordRepository;
import com.cts.repository.MedicalReferralRepository;
import com.cts.repository.spec.ReferralSpecification;
import com.cts.security.CurrentUser;
import com.cts.service.AuditLogService;
import com.cts.service.NotificationService;
import com.cts.service.ReferralService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final MedicalReferralRepository referralRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final ReferralMapper referralMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    private static final String ENTITY_TYPE = "MedicalReferral";

    @Override
    @Transactional
    public ReferralResponse createReferral(ReferralRequest request) {
        log.info("Creating referral for employee {} from health record {}",
                request.getEmployeeId(), request.getHealthRecordId());

        HealthRecord record = healthRecordRepository.findById(request.getHealthRecordId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Health record not found with id: " + request.getHealthRecordId()));

        Long recordEmployeeId = record.getEmployee().getUserId();
        if (!request.getEmployeeId().equals(recordEmployeeId)) {
            throw new IllegalArgumentException(
                    "EmployeeID must match the EmployeeID on the linked health record ("
                            + recordEmployeeId + ")");
        }

        // Existence of healthRecord + employee is validated inside the mapper (loads or throws)
        MedicalReferral referral = referralMapper.toEntity(request);
        referral.setStatus(ReferralStatus.REFERRED);

        MedicalReferral saved = referralRepository.save(referral);
        auditLogService.record(currentUser.id(), "CREATE_REFERRAL", ENTITY_TYPE, saved.getReferralId());

        log.info("Referral created with id: {}", saved.getReferralId());
        return referralMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralResponse getReferralById(Long referralId) {
        MedicalReferral referral = findOrThrow(referralId);
        enforcePii(referral.getEmployee().getUserId());   // Story 22 PII
        return referralMapper.toResponse(referral);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferralResponse> searchReferrals(Long employeeId, Long healthRecordId, ReferralStatus status,
                                                  String referredToSpeciality, LocalDate fromDate, LocalDate toDate) {
        Long effectiveEmployeeId = employeeId;
        if (!currentUser.hasAnyRole(Role.OH_NURSE, Role.EHS_MANAGER)) {
            effectiveEmployeeId = currentUser.id();
        }
        var spec = ReferralSpecification.build(
                effectiveEmployeeId, healthRecordId, status, referredToSpeciality, fromDate, toDate);
        return referralRepository.findAll(spec).stream()
                .map(referralMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReferralResponse updateOutcome(Long referralId, ReferralOutcomeRequest request) {
        log.info("Updating referral {} to status {}", referralId, request.getStatus());
        MedicalReferral referral = findOrThrow(referralId);
        validateTransition(referral.getStatus(), request.getStatus());

        if ((request.getStatus() == ReferralStatus.ATTENDED || request.getStatus() == ReferralStatus.CLOSED)
                && (request.getOutcomeSummary() == null || request.getOutcomeSummary().isBlank())) {
            throw new IllegalArgumentException(
                    "OutcomeSummary is required when status becomes Attended or Closed");
        }
        if (request.getOutcomeSummary() != null) {
            referral.setOutcomeSummary(request.getOutcomeSummary());
        }

        referral.setStatus(request.getStatus());
        MedicalReferral updated = referralRepository.save(referral);
        auditLogService.record(currentUser.id(),
                "UPDATE_REFERRAL_STATUS_" + request.getStatus().name(), ENTITY_TYPE, updated.getReferralId());

        // Story 22: FollowUpRequired -> Notification (Category = Health)
        if (request.getStatus() == ReferralStatus.FOLLOW_UP_REQUIRED) {
            Long employeeId = updated.getEmployee().getUserId();
            notificationService.create(
                    employeeId,
                    "Follow-up required for referral " + updated.getReferralId()
                            + " (employee " + employeeId + ")",
                    NotificationCategory.HEALTH);
        }

        return referralMapper.toResponse(updated);
    }

    private void enforcePii(Long referralEmployeeId) {
        if (currentUser.hasAnyRole(Role.OH_NURSE, Role.EHS_MANAGER)) {
            return;
        }
        if (!currentUser.id().equals(referralEmployeeId)) {
            throw new AccessForbiddenException(
                    "Referral records are accessible only to OH Nurse, EHS Manager, or the employee themselves");
        }
    }

    private void validateTransition(ReferralStatus current, ReferralStatus next) {
        boolean ok = switch (current) {
            case REFERRED -> next == ReferralStatus.ATTENDED;
            case ATTENDED -> next == ReferralStatus.FOLLOW_UP_REQUIRED || next == ReferralStatus.CLOSED;
            case FOLLOW_UP_REQUIRED -> next == ReferralStatus.ATTENDED || next == ReferralStatus.CLOSED;
            case CLOSED -> false;
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid referral status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private MedicalReferral findOrThrow(Long referralId) {
        return referralRepository.findById(referralId)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found with id: " + referralId));
    }
}