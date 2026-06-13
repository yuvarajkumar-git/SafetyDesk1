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

import com.cts.dto.request.InvestigationRequest;
import com.cts.dto.request.InvestigationUpdateRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.InvestigationResponse;
import com.cts.enums.InvestigationStatus;
import com.cts.service.InvestigationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for IncidentInvestigation (Story 13).
 * Base path: /api/investigations
 */
@Slf4j
@RestController
@RequestMapping("/api/investigations")
@RequiredArgsConstructor
public class InvestigationController {

    private final InvestigationService investigationService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvestigationResponse>> create(
            @Valid @RequestBody InvestigationRequest request) {
        InvestigationResponse created = investigationService.createInvestigation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Investigation created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvestigationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Investigation retrieved successfully", investigationService.getInvestigationById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestigationResponse>>> search(
            @RequestParam(required = false) Long incidentId,
            @RequestParam(required = false) InvestigationStatus status) {
        List<InvestigationResponse> results;
        if (incidentId != null) {
            results = investigationService.getByIncidentId(incidentId);
        } else if (status != null) {
            results = investigationService.getByStatus(status);
        } else {
            results = investigationService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.success("Investigations retrieved successfully", results));
    }

    @PutMapping("/{id}/findings")
    public ResponseEntity<ApiResponse<InvestigationResponse>> updateFindings(
            @PathVariable Long id, @Valid @RequestBody InvestigationUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Investigation findings updated successfully", investigationService.updateFindings(id, request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvestigationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam InvestigationStatus status,
            @RequestParam(required = false, defaultValue = "false") boolean correctiveActionsNeeded) {
        return ResponseEntity.ok(ApiResponse.success(
                "Investigation status updated successfully",
                investigationService.updateStatus(id, status, correctiveActionsNeeded)));
    }
}