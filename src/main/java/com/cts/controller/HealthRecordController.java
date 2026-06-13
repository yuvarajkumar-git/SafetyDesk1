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

import com.cts.dto.request.HealthRecordRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.HealthRecordResponse;
import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HealthRecordStatus;
import com.cts.service.HealthRecordService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for HealthRecord (Story 21).
 * Base path: /api/health-records
 */
@Slf4j
@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @PostMapping
    public ResponseEntity<ApiResponse<HealthRecordResponse>> create(
            @Valid @RequestBody HealthRecordRequest request) {
        HealthRecordResponse created = healthRecordService.createHealthRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Health record created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthRecordResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Health record retrieved successfully", healthRecordService.getHealthRecordById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HealthRecordResponse>>> search(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) AssessmentType assessmentType,
            @RequestParam(required = false) FitnessDecision fitnessDecision,
            @RequestParam(required = false) HealthRecordStatus status,
            @RequestParam(required = false) Long conductedById,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate assessmentFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate assessmentTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextTo) {
        List<HealthRecordResponse> results = healthRecordService.searchHealthRecords(
                employeeId, assessmentType, fitnessDecision, status, conductedById,
                assessmentFrom, assessmentTo, nextFrom, nextTo);
        return ResponseEntity.ok(ApiResponse.success("Health records retrieved successfully", results));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<HealthRecordResponse>> updateStatus(
            @PathVariable Long id, @RequestParam HealthRecordStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Health record status updated successfully", healthRecordService.updateStatus(id, status)));
    }

    @PutMapping("/remind-upcoming")
    public ResponseEntity<ApiResponse<Integer>> remindUpcoming(
            @RequestParam(defaultValue = "7") int withinDays) {
        int count = healthRecordService.remindUpcomingAssessments(withinDays);
        return ResponseEntity.ok(ApiResponse.success("Surveillance reminders sent", count));
    }
}