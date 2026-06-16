package com.cts.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.cts.dto.request.RiskAssessmentRequest;
import com.cts.dto.response.RiskAssessmentResponse;
import com.cts.entity.HazardRecord;
import com.cts.entity.RiskAssessment;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.HazardRecordRepository;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RiskAssessmentMapper {

    private final HazardRecordRepository hazardRepository;
    private final UserRepository userRepository;

    // Copies plain fields + resolves relationships. riskRating, riskLevel and status are set by the service.
    public RiskAssessment toEntity(RiskAssessmentRequest request) {
        HazardRecord hazard = hazardRepository.findById(request.getHazardId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hazard not found with id: " + request.getHazardId()));
        User assessedBy = userRepository.findById(request.getAssessedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessor (User) not found with id: " + request.getAssessedById()));

        return RiskAssessment.builder()
                .hazard(hazard)
                .taskDescription(request.getTaskDescription())
                .likelihood(request.getLikelihood())
                .severity(request.getSeverity())
                .existingControls(request.getExistingControls())
                .additionalControls(request.getAdditionalControls())
                .residualRisk(request.getResidualRisk())
                .assessedBy(assessedBy)
                .assessmentDate(request.getAssessmentDate() != null
                        ? request.getAssessmentDate() : LocalDate.now())
                .build();
    }

    public RiskAssessmentResponse toResponse(RiskAssessment ra) {
        return RiskAssessmentResponse.builder()
                .assessmentId(ra.getAssessmentId())
                .hazardId(ra.getHazard() != null ? ra.getHazard().getHazardId() : null)
                .taskDescription(ra.getTaskDescription())
                .likelihood(ra.getLikelihood())
                .severity(ra.getSeverity())
                .riskRating(ra.getRiskRating())
                .riskLevel(ra.getRiskLevel())
                .existingControls(ra.getExistingControls())
                .additionalControls(ra.getAdditionalControls())
                .residualRisk(ra.getResidualRisk())
                .assessedById(ra.getAssessedBy() != null ? ra.getAssessedBy().getUserId() : null)
                .assessmentDate(ra.getAssessmentDate())
                .status(ra.getStatus())
                .createdAt(ra.getCreatedAt())
                .updatedAt(ra.getUpdatedAt())
                .build();
    }
}