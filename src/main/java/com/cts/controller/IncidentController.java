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

import com.cts.dto.request.IncidentRequest;
import com.cts.dto.request.InvestigatorAssignmentRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.IncidentResponse;
import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.Severity;
import com.cts.service.IncidentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for IncidentReport (Story 12).
 * Base path: /api/incidents
 */
@Slf4j
@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<ApiResponse<IncidentResponse>> createIncident(
            @Valid @RequestBody IncidentRequest request) {
        log.info("POST /api/incidents");
        IncidentResponse created = incidentService.createIncident(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Incident reported successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponse>> getIncident(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Incident retrieved successfully", incidentService.getIncidentById(id)));
    }

    // Filtering endpoint (Story 12). All params optional.
    @GetMapping
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> searchIncidents(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) IncidentType incidentType,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) Long reportedById,
            @RequestParam(required = false) Long assignedInvestigatorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.info("GET /api/incidents (search)");
        List<IncidentResponse> results = incidentService.searchIncidents(
                siteId, incidentType, severity, status, reportedById, assignedInvestigatorId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Incidents retrieved successfully", results));
    }

    // Assign investigator -> moves Reported to UnderInvestigation
    @PutMapping("/{id}/assign-investigator")
    public ResponseEntity<ApiResponse<IncidentResponse>> assignInvestigator(
            @PathVariable Long id,
            @Valid @RequestBody InvestigatorAssignmentRequest request) {
        log.info("PUT /api/incidents/{}/assign-investigator", id);
        return ResponseEntity.ok(ApiResponse.success(
                "Investigator assigned successfully", incidentService.assignInvestigator(id, request)));
    }

    // Generic status transition (CAPAAssigned, Closed, etc.)
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<IncidentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam IncidentStatus status) {
        log.info("PUT /api/incidents/{}/status -> {}", id, status);
        return ResponseEntity.ok(ApiResponse.success(
                "Incident status updated successfully", incidentService.updateStatus(id, status)));
    }
}