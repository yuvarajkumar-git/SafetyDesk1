package com.cts.dto.request;

import java.time.LocalDate;

import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;

/**
 * Payload to record a health assessment (Story 21).
 * NextAssessmentDate rules (future, required for Periodic/PostIncident)
 * are enforced in the service.
 */
@Data
public class HealthRecordRequest {

    @NotNull(message = "EmployeeID is required")
    private Long employeeId;

    @NotNull(message = "AssessmentType is required")
    private AssessmentType assessmentType;

    @NotNull(message = "AssessmentDate is required")
    @PastOrPresent(message = "AssessmentDate cannot be in the future")
    private LocalDate assessmentDate;

    @NotNull(message = "ConductedByID is required")
    private Long conductedById;

    @NotNull(message = "FitnessDecision is required")
    private FitnessDecision fitnessDecision;

    private LocalDate nextAssessmentDate;
}