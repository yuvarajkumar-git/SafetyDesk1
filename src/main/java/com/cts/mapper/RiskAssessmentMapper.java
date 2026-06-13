package com.cts.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.cts.dto.request.RiskAssessmentRequest;
import com.cts.dto.response.RiskAssessmentResponse;
import com.cts.entity.RiskAssessment;

@Component
public class RiskAssessmentMapper {

    // Copies plain fields. riskRating, riskLevel and status are set by the service.
    public RiskAssessment toEntity(RiskAssessmentRequest request) {
        return RiskAssessment.builder()
                .hazardId(request.getHazardId())
                .taskDescription(request.getTaskDescription())
                .likelihood(request.getLikelihood())
                .severity(request.getSeverity())
                .existingControls(request.getExistingControls())
                .additionalControls(request.getAdditionalControls())
                .residualRisk(request.getResidualRisk())
                .assessedById(request.getAssessedById())
                .assessmentDate(request.getAssessmentDate() != null
                        ? request.getAssessmentDate() : LocalDate.now())
                .build();
    }

    public RiskAssessmentResponse toResponse(RiskAssessment ra) {
        return RiskAssessmentResponse.builder()
                .assessmentId(ra.getAssessmentId())
                .hazardId(ra.getHazardId())
                .taskDescription(ra.getTaskDescription())
                .likelihood(ra.getLikelihood())
                .severity(ra.getSeverity())
                .riskRating(ra.getRiskRating())
                .riskLevel(ra.getRiskLevel())
                .existingControls(ra.getExistingControls())
                .additionalControls(ra.getAdditionalControls())
                .residualRisk(ra.getResidualRisk())
                .assessedById(ra.getAssessedById())
                .assessmentDate(ra.getAssessmentDate())
                .status(ra.getStatus())
                .createdAt(ra.getCreatedAt())
                .updatedAt(ra.getUpdatedAt())
                .build();
    }
}