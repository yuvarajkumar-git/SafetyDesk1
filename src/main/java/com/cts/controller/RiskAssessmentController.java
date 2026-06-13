package com.cts.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.request.RiskAssessmentRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.RiskAssessmentResponse;
import com.cts.enums.RiskAssessmentStatus;
import com.cts.service.RiskAssessmentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for RiskAssessment (Story 16).
 * Base path: /api/risk-assessments
 */
@Slf4j
@RestController
@RequestMapping("/api/risk-assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService assessmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> create(
            @Valid @RequestBody RiskAssessmentRequest request) {
        RiskAssessmentResponse created = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Risk assessment created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Risk assessment retrieved successfully", assessmentService.getAssessmentById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RiskAssessmentResponse>>> search(
            @RequestParam(required = false) Long hazardId,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(required = false) RiskAssessmentStatus status,
            @RequestParam(required = false) Long assessedById,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<RiskAssessmentResponse> results = assessmentService.searchAssessments(
                hazardId, minRating, maxRating, status, assessedById, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Risk assessments retrieved successfully", results));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Risk assessment approved successfully", assessmentService.approveAssessment(id)));
    }
}