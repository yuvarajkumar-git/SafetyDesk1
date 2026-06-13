package com.cts.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.RiskAssessmentRequest;
import com.cts.dto.response.RiskAssessmentResponse;
import com.cts.entity.RiskAssessment;
import com.cts.enums.RiskAssessmentStatus;
import com.cts.enums.RiskLevel;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.RiskAssessmentMapper;
import com.cts.repository.HazardRecordRepository;
import com.cts.repository.RiskAssessmentRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.RiskAssessmentSpecification;
import com.cts.service.AuditLogService;
import com.cts.service.RiskAssessmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentRepository assessmentRepository;
    private final HazardRecordRepository hazardRepository;
    private final UserRepository userRepository;
    private final RiskAssessmentMapper assessmentMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "RiskAssessment";

    @Override
    @Transactional
    public RiskAssessmentResponse createAssessment(RiskAssessmentRequest request) {
        log.info("Creating risk assessment for hazard: {}", request.getHazardId());

        // HazardID must reference a valid hazard
        if (!hazardRepository.existsById(request.getHazardId())) {
            throw new ResourceNotFoundException("Hazard not found with id: " + request.getHazardId());
        }
        // AssessedByID must reference a valid user
        if (!userRepository.existsById(request.getAssessedById())) {
            throw new ResourceNotFoundException("Assessor (User) not found with id: " + request.getAssessedById());
        }

        RiskAssessment assessment = assessmentMapper.toEntity(request);

        // Story 16: RiskRating = Likelihood x Severity, then derive the band
        int rating = request.getLikelihood() * request.getSeverity();
        assessment.setRiskRating(rating);
        assessment.setRiskLevel(deriveRiskLevel(rating));
        assessment.setStatus(RiskAssessmentStatus.DRAFT); // lifecycle start

        // Story 16: a new assessment supersedes the previous active one for this hazard
        supersedePreviousAssessments(request.getHazardId());

        RiskAssessment saved = assessmentRepository.save(assessment);
        auditLogService.record(saved.getAssessedById(), "CREATE_RISK_ASSESSMENT", ENTITY_TYPE, saved.getAssessmentId());

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
        // RBAC: only Safety Officer / EHS Manager may approve (enforced in security step)

        RiskAssessment assessment = findOrThrow(assessmentId);

        // Story 16 lifecycle: only Draft -> Approved
        if (assessment.getStatus() != RiskAssessmentStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only a Draft assessment can be approved. Current status: "
                            + assessment.getStatus().getLabel());
        }

        assessment.setStatus(RiskAssessmentStatus.APPROVED);
        RiskAssessment updated = assessmentRepository.save(assessment);
        auditLogService.record(updated.getAssessedById(), "APPROVE_RISK_ASSESSMENT", ENTITY_TYPE, updated.getAssessmentId());
        return assessmentMapper.toResponse(updated);
    }

    // Story 16 thresholds: 1-4 Low, 5-9 Medium, 10-15 High, 16-25 Critical
    private RiskLevel deriveRiskLevel(int rating) {
        if (rating <= 4)  return RiskLevel.LOW;
        if (rating <= 9)  return RiskLevel.MEDIUM;
        if (rating <= 15) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }

    // Story 16: mark all currently Draft/Approved assessments for this hazard as Superseded
    private void supersedePreviousAssessments(Long hazardId) {
        List<RiskAssessment> previous = assessmentRepository.findByHazardIdAndStatus(hazardId, RiskAssessmentStatus.DRAFT);
        previous.addAll(assessmentRepository.findByHazardIdAndStatus(hazardId, RiskAssessmentStatus.APPROVED));
        for (RiskAssessment old : previous) {
            old.setStatus(RiskAssessmentStatus.SUPERSEDED);
            assessmentRepository.save(old);
            auditLogService.record(old.getAssessedById(),
                    "SUPERSEDE_RISK_ASSESSMENT", ENTITY_TYPE, old.getAssessmentId());
            log.info("Superseded previous assessment {} for hazard {}", old.getAssessmentId(), hazardId);
        }
    }

    private RiskAssessment findOrThrow(Long assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Risk assessment not found with id: " + assessmentId));
    }
}