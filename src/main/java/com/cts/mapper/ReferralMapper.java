package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.ReferralRequest;
import com.cts.dto.response.ReferralResponse;
import com.cts.entity.MedicalReferral;

@Component
public class ReferralMapper {

    public MedicalReferral toEntity(ReferralRequest request) {
        return MedicalReferral.builder()
                .healthRecordId(request.getHealthRecordId())
                .employeeId(request.getEmployeeId())
                .referralReason(request.getReferralReason())
                .referredToSpeciality(request.getReferredToSpeciality())
                .referralDate(request.getReferralDate())
                // status set by the service
                .build();
    }

    public ReferralResponse toResponse(MedicalReferral ref) {
        return ReferralResponse.builder()
                .referralId(ref.getReferralId())
                .healthRecordId(ref.getHealthRecordId())
                .employeeId(ref.getEmployeeId())
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