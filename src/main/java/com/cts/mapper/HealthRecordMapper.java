package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.HealthRecordRequest;
import com.cts.dto.response.HealthRecordResponse;
import com.cts.entity.HealthRecord;
import com.cts.enums.FitnessDecision;

@Component
public class HealthRecordMapper {

    public HealthRecord toEntity(HealthRecordRequest request) {
        return HealthRecord.builder()
                .employeeId(request.getEmployeeId())
                .assessmentType(request.getAssessmentType())
                .assessmentDate(request.getAssessmentDate())
                .conductedById(request.getConductedById())
                .fitnessDecision(request.getFitnessDecision())
                .nextAssessmentDate(request.getNextAssessmentDate())
                // status set by the service
                .build();
    }

    public HealthRecordResponse toResponse(HealthRecord record) {
        return HealthRecordResponse.builder()
                .healthRecordId(record.getHealthRecordId())
                .employeeId(record.getEmployeeId())
                .assessmentType(record.getAssessmentType())
                .assessmentDate(record.getAssessmentDate())
                .conductedById(record.getConductedById())
                .fitnessDecision(record.getFitnessDecision())
                .nextAssessmentDate(record.getNextAssessmentDate())
                .status(record.getStatus())
                .restrictedDuty(isRestrictedDuty(record.getFitnessDecision()))
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    // Story 21: TemporaryUnfit or PermanentlyUnfit -> flagged for restricted duty
    private boolean isRestrictedDuty(FitnessDecision decision) {
        return decision == FitnessDecision.TEMPORARY_UNFIT
                || decision == FitnessDecision.PERMANENTLY_UNFIT;
    }
}