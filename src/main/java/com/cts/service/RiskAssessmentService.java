package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.request.RiskAssessmentRequest;
import com.cts.dto.response.RiskAssessmentResponse;
import com.cts.enums.RiskAssessmentStatus;

public interface RiskAssessmentService {

    RiskAssessmentResponse createAssessment(RiskAssessmentRequest request);

    RiskAssessmentResponse getAssessmentById(Long assessmentId);

    List<RiskAssessmentResponse> searchAssessments(Long hazardId, Integer minRating, Integer maxRating,
                                                   RiskAssessmentStatus status, Long assessedById,
                                                   LocalDate fromDate, LocalDate toDate);

    RiskAssessmentResponse approveAssessment(Long assessmentId);
}