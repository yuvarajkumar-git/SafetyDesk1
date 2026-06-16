package com.cts.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.RiskAssessmentRequest;
import com.cts.dto.response.RiskAssessmentResponse;
import com.cts.entity.RiskAssessment;
import com.cts.entity.User;
import com.cts.enums.RiskAssessmentStatus;
import com.cts.enums.RiskLevel;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.RiskAssessmentMapper;
import com.cts.repository.RiskAssessmentRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.RiskAssessmentSpecification;
import com.cts.security.CurrentUser;
import com.cts.service.AuditLogService;
import com.cts.service.RiskAssessmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentRepository assessmentRepository;
    private final UserRepository userRepository;
    private final RiskAssessmentMapper assessmentMapper;
    private final AuditLogService auditLogService;
    private final CurrentUser currentUser;

    private static final String ENTITY_TYPE = "RiskAssessment";

    @Override
    @Transactional
    public RiskAssessmentResponse createAssessment(RiskAssessmentRequest request) {
        log.info("Creating risk assessment for hazard: {}", request.getHazardId());

        // Hazard existence is validated inside the mapper (loads or throws)
        RiskAssessment assessment = assessmentMapper.toEntity(request);

        // Story 16: AssessedByID from the authenticated user
        User assessedBy = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.id()));
        assessment.setAssessedBy(assessedBy);

        // Story 16: RiskRating = Likelihood x Severity, then derive the band
        int rating = request.getLikelihood() * request.getSeverity();
        assessment.setRiskRating(rating);
        assessment.setRiskLevel(deriveRiskLevel(rating));
        assessment.setStatus(RiskAssessmentStatus.DRAFT); // lifecycle start

        // Story 16: a new assessment supersedes the previous active one for this hazard
        supersedePreviousAssessments(request.getHazardId());

        RiskAssessment saved = assessmentRepository.save(assessment);
        auditLogService.record(currentUser.id(), "CREATE_RISK_ASSESSMENT", ENTITY_TYPE, saved.getAssessmentId());

        log.info("Risk assessment created with id {} (rating {}, level {})",
                saved.getAssessmentId(), rating, saved.getRiskLevel().getLabel());
        return assessmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessmentResponse getAssessmentById(Long assessmentId) {
        return assessmentMapper.toResponse(findOrThrow(assessmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse> searchAssessments(Long hazardId, Integer minRating, Integer maxRating,
                                                          RiskAssessmentStatus status, Long assessedById,
                                                          LocalDate fromDate, LocalDate toDate) {
        var spec = RiskAssessmentSpecification.build(
                hazardId, minRating, maxRating, status, assessedById, fromDate, toDate);
        return assessmentRepository.findAll(spec).stream()
                .map(assessmentMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RiskAssessmentResponse approveAssessment(Long assessmentId) {
        log.info("Approving risk assessment: {}", assessmentId);

        RiskAssessment assessment = findOrThrow(assessmentId);

        if (assessment.getStatus() != RiskAssessmentStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only a Draft assessment can be approved. Current status: "
                            + assessment.getStatus().getLabel());
        }

        assessment.setStatus(RiskAssessmentStatus.APPROVED);
        RiskAssessment updated = assessmentRepository.save(assessment);
        auditLogService.record(currentUser.id(), "APPROVE_RISK_ASSESSMENT", ENTITY_TYPE, updated.getAssessmentId());
        return assessmentMapper.toResponse(updated);
    }

    private RiskLevel deriveRiskLevel(int rating) {
        if (rating <= 4)  return RiskLevel.LOW;
        if (rating <= 9)  return RiskLevel.MEDIUM;
        if (rating <= 15) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }

    private void supersedePreviousAssessments(Long hazardId) {
        List<RiskAssessment> previous =
                assessmentRepository.findByHazard_HazardIdAndStatus(hazardId, RiskAssessmentStatus.DRAFT);
        previous.addAll(assessmentRepository.findByHazard_HazardIdAndStatus(hazardId, RiskAssessmentStatus.APPROVED));
        for (RiskAssessment old : previous) {
            old.setStatus(RiskAssessmentStatus.SUPERSEDED);
            assessmentRepository.save(old);
            auditLogService.record(currentUser.id(),
                    "SUPERSEDE_RISK_ASSESSMENT", ENTITY_TYPE, old.getAssessmentId());
            log.info("Superseded previous assessment {} for hazard {}", old.getAssessmentId(), hazardId);
        }
    }

    private RiskAssessment findOrThrow(Long assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Risk assessment not found with id: " + assessmentId));
    }
}