package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.HealthRecordRequest;
import com.cts.dto.response.HealthRecordResponse;
import com.cts.entity.HealthRecord;
import com.cts.entity.User;
import com.cts.enums.FitnessDecision;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HealthRecordMapper {

    private final UserRepository userRepository;

    public HealthRecord toEntity(HealthRecordRequest request) {
        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee (User) not found with id: " + request.getEmployeeId()));
        User conductedBy = userRepository.findById(request.getConductedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ConductedBy (User) not found with id: " + request.getConductedById()));

        return HealthRecord.builder()
                .employee(employee)
                .assessmentType(request.getAssessmentType())
                .assessmentDate(request.getAssessmentDate())
                .conductedBy(conductedBy)
                .fitnessDecision(request.getFitnessDecision())
                .nextAssessmentDate(request.getNextAssessmentDate())
                // status set by the service
                .build();
    }

    public HealthRecordResponse toResponse(HealthRecord record) {
        return HealthRecordResponse.builder()
                .healthRecordId(record.getHealthRecordId())
                .employeeId(record.getEmployee() != null ? record.getEmployee().getUserId() : null)
                .assessmentType(record.getAssessmentType())
                .assessmentDate(record.getAssessmentDate())
                .conductedById(record.getConductedBy() != null ? record.getConductedBy().getUserId() : null)
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