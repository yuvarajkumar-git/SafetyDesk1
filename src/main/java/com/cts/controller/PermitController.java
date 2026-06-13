package com.cts.controller;

import java.time.LocalDateTime;
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

import com.cts.dto.request.PermitApprovalRequest;
import com.cts.dto.request.PermitRequest;
import com.cts.dto.request.PermitUpdateRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.PermitResponse;
import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;
import com.cts.service.PermitService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for WorkPermit (Story 19).
 * Base path: /api/permits
 */
@Slf4j
@RestController
@RequestMapping("/api/permits")
@RequiredArgsConstructor
public class PermitController {

    private final PermitService permitService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermitResponse>> create(
            @Valid @RequestBody PermitRequest request) {
        PermitResponse created = permitService.createPermit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permit created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermitResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permit retrieved successfully", permitService.getPermitById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermitResponse>>> search(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) PermitType permitType,
            @RequestParam(required = false) PermitStatus status,
            @RequestParam(required = false) String workLocation,
            @RequestParam(required = false) Long issuedToId,
            @RequestParam(required = false) Long approvedById,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDateTime) {
        List<PermitResponse> results = permitService.searchPermits(
                siteId, permitType, status, workLocation, issuedToId, approvedById, fromDateTime, toDateTime);
        return ResponseEntity.ok(ApiResponse.success("Permits retrieved successfully", results));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermitResponse>> update(
            @PathVariable Long id, @Valid @RequestBody PermitUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permit updated successfully", permitService.updatePermit(id, request)));
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<PermitResponse>> submit(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permit submitted for approval", permitService.submitForApproval(id)));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<PermitResponse>> approve(
            @PathVariable Long id, @Valid @RequestBody PermitApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permit approved successfully", permitService.approvePermit(id, request)));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PermitResponse>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permit activated successfully", permitService.activatePermit(id)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PermitResponse>> updateStatus(
            @PathVariable Long id, @RequestParam PermitStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permit status updated successfully", permitService.updateStatus(id, status)));
    }

    @PutMapping("/mark-expired")
    public ResponseEntity<ApiResponse<Integer>> markExpired() {
        int count = permitService.markExpiredPermits();
        return ResponseEntity.ok(ApiResponse.success("Expired permits flagged", count));
    }
}