package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.ReferralRequest;
import com.cts.dto.response.ReferralResponse;
import com.cts.entity.HealthRecord;
import com.cts.entity.MedicalReferral;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.HealthRecordRepository;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReferralMapper {

    private final HealthRecordRepository healthRecordRepository;
    private final UserRepository userRepository;

    public MedicalReferral toEntity(ReferralRequest request) {
        HealthRecord healthRecord = healthRecordRepository.findById(request.getHealthRecordId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Health record not found with id: " + request.getHealthRecordId()));
        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee (User) not found with id: " + request.getEmployeeId()));

        return MedicalReferral.builder()
                .healthRecord(healthRecord)
                .employee(employee)
                .referralReason(request.getReferralReason())
                .referredToSpeciality(request.getReferredToSpeciality())
                .referralDate(request.getReferralDate())
                // status set by the service
                .build();
    }

    public ReferralResponse toResponse(MedicalReferral ref) {
        return ReferralResponse.builder()
                .referralId(ref.getReferralId())
                .healthRecordId(ref.getHealthRecord() != null ? ref.getHealthRecord().getHealthRecordId() : null)
                .employeeId(ref.getEmployee() != null ? ref.getEmployee().getUserId() : null)
                .referralReason(ref.getReferralReason())
                .referredToSpeciality(ref.getReferredToSpeciality())
                .referralDate(ref.getReferralDate())
                .outcomeSummary(ref.getOutcomeSummary())
                .status(ref.getStatus())
                .createdAt(ref.getCreatedAt())
                .updatedAt(ref.getUpdatedAt())
                .build();
    }
}