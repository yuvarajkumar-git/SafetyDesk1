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

import com.cts.dto.request.FindingBatchRequest;
import com.cts.dto.request.FindingRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.FindingResponse;
import com.cts.enums.FindingStatus;
import com.cts.enums.FindingType;
import com.cts.enums.RiskLevel;
import com.cts.service.FindingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for InspectionFinding (Story 18).
 * Base path: /api/findings
 */
@Slf4j
@RestController
@RequestMapping("/api/findings")
@RequiredArgsConstructor
public class FindingController {

    private final FindingService findingService;

    @PostMapping
    public ResponseEntity<ApiResponse<FindingResponse>> create(
            @Valid @RequestBody FindingRequest request) {
        FindingResponse created = findingService.createFinding(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Finding recorded successfully", created));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<FindingResponse>>> createBatch(
            @Valid @RequestBody FindingBatchRequest request) {
        List<FindingResponse> created = findingService.createFindingsBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Findings recorded successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FindingResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Finding retrieved successfully", findingService.getFindingById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FindingResponse>>> search(
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) FindingType findingType,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) FindingStatus status,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<FindingResponse> results = findingService.searchFindings(
                scheduleId, findingType, riskLevel, status, assignedToId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Findings retrieved successfully", results));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FindingResponse>> updateStatus(
            @PathVariable Long id, @RequestParam FindingStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Finding status updated successfully", findingService.updateStatus(id, status)));
    }

    @PutMapping("/mark-overdue")
    public ResponseEntity<ApiResponse<Integer>> markOverdue() {
        int count = findingService.markOverdueFindings();
        return ResponseEntity.ok(ApiResponse.success("Overdue findings flagged", count));
    }
}