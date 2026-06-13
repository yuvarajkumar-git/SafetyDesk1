package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.RiskAssessmentStatus;
import com.cts.enums.RiskLevel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskAssessmentResponse {

    private Long assessmentId;
    private Long hazardId;
    private String taskDescription;
    private Integer likelihood;
    private Integer severity;
    private Integer riskRating;   // numeric score 1-25
    private RiskLevel riskLevel;  // band Low/Medium/High/Critical
    private String existingControls;
    private String additionalControls;
    private String residualRisk;
    private Long assessedById;
    private LocalDate assessmentDate;
    private RiskAssessmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}