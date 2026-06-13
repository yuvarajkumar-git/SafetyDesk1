package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HealthRecordStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthRecordResponse {

    private Long healthRecordId;
    private Long employeeId;
    private AssessmentType assessmentType;
    private LocalDate assessmentDate;
    private Long conductedById;
    private FitnessDecision fitnessDecision;
    private LocalDate nextAssessmentDate;
    private HealthRecordStatus status;
    private boolean restrictedDuty; // derived flag (Story 21)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}