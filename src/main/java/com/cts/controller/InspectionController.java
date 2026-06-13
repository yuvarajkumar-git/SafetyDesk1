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

import com.cts.dto.request.InspectionRequest;
import com.cts.dto.request.RecurringInspectionRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.InspectionResponse;
import com.cts.enums.InspectionStatus;
import com.cts.enums.InspectionType;
import com.cts.service.InspectionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for InspectionSchedule (Story 17).
 * Base path: /api/inspections
 */
@Slf4j
@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<InspectionResponse>> schedule(
            @Valid @RequestBody InspectionRequest request) {
        InspectionResponse created = inspectionService.scheduleInspection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inspection scheduled successfully", created));
    }

    @PostMapping("/recurring")
    public ResponseEntity<ApiResponse<List<InspectionResponse>>> scheduleRecurring(
            @Valid @RequestBody RecurringInspectionRequest request) {
        List<InspectionResponse> created = inspectionService.scheduleRecurring(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recurring inspections scheduled successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Inspection retrieved successfully", inspectionService.getInspectionById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InspectionResponse>>> search(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) InspectionType inspectionType,
            @RequestParam(required = false) Long assignedOfficerId,
            @RequestParam(required = false) InspectionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<InspectionResponse> results = inspectionService.searchInspections(
                siteId, inspectionType, assignedOfficerId, status, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Inspections retrieved successfully", results));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InspectionResponse>> updateStatus(
            @PathVariable Long id, @RequestParam InspectionStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Inspection status updated successfully", inspectionService.updateStatus(id, status)));
    }

    @PutMapping("/mark-missed")
    public ResponseEntity<ApiResponse<Integer>> markMissed() {
        int count = inspectionService.markMissedInspections();
        return ResponseEntity.ok(ApiResponse.success("Missed inspections flagged", count));
    }
}