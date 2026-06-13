package com.cts.controller;

import java.util.List;

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

import com.cts.dto.request.CorrectiveActionBatchRequest;
import com.cts.dto.request.CorrectiveActionRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.CorrectiveActionResponse;
import com.cts.enums.CorrectiveActionStatus;
import com.cts.service.CorrectiveActionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for CorrectiveAction (Story 14).
 * Base path: /api/corrective-actions
 */
@Slf4j
@RestController
@RequestMapping("/api/corrective-actions")
@RequiredArgsConstructor
public class CorrectiveActionController {

    private final CorrectiveActionService actionService;

    @PostMapping
    public ResponseEntity<ApiResponse<CorrectiveActionResponse>> create(
            @Valid @RequestBody CorrectiveActionRequest request) {
        CorrectiveActionResponse created = actionService.createAction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Corrective action created successfully", created));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<CorrectiveActionResponse>>> createBatch(
            @Valid @RequestBody CorrectiveActionBatchRequest request) {
        List<CorrectiveActionResponse> created = actionService.createActionsBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Corrective actions created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CorrectiveActionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Corrective action retrieved successfully", actionService.getActionById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CorrectiveActionResponse>>> search(
            @RequestParam(required = false) Long incidentId,
            @RequestParam(required = false) CorrectiveActionStatus status) {
        List<CorrectiveActionResponse> results;
        if (incidentId != null) {
            results = actionService.getByIncidentId(incidentId);
        } else if (status != null) {
            results = actionService.getByStatus(status);
        } else {
            results = List.of();
        }
        return ResponseEntity.ok(ApiResponse.success("Corrective actions retrieved successfully", results));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CorrectiveActionResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam CorrectiveActionStatus status,
            @RequestParam(required = false) Long verifiedById) {
        return ResponseEntity.ok(ApiResponse.success(
                "Corrective action status updated successfully",
                actionService.updateStatus(id, status, verifiedById)));
    }

    // Manual trigger for overdue detection (later this can run on a schedule)
    @PutMapping("/mark-overdue")
    public ResponseEntity<ApiResponse<Integer>> markOverdue() {
        int count = actionService.markOverdueActions();
        return ResponseEntity.ok(ApiResponse.success("Overdue corrective actions flagged", count));
    }
}