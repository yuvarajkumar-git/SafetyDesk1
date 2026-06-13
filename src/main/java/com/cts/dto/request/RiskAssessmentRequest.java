package com.cts.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;

/**
 * Payload to create a risk assessment (Story 16).
 * RiskRating / RiskLevel are NOT accepted - they are calculated server-side.
 */
@Data
public class RiskAssessmentRequest {

    @NotNull(message = "HazardID is required")
    private Long hazardId;

    private String taskDescription;

    @NotNull(message = "Likelihood is required")
    @Min(value = 1, message = "Likelihood must be between 1 and 5")
    @Max(value = 5, message = "Likelihood must be between 1 and 5")
    private Integer likelihood;

    @NotNull(message = "Severity is required")
    @Min(value = 1, message = "Severity must be between 1 and 5")
    @Max(value = 5, message = "Severity must be between 1 and 5")
    private Integer severity;

    private String existingControls;
    private String additionalControls;
    private String residualRisk;

    @NotNull(message = "AssessedByID is required")
    private Long assessedById;

    @PastOrPresent(message = "AssessmentDate cannot be in the future")
    private LocalDate assessmentDate;
}