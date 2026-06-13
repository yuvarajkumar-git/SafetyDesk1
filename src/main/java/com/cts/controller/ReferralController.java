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

import com.cts.dto.request.ReferralOutcomeRequest;
import com.cts.dto.request.ReferralRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.ReferralResponse;
import com.cts.enums.ReferralStatus;
import com.cts.service.ReferralService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for MedicalReferral (Story 22).
 * Base path: /api/referrals
 */
@Slf4j
@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReferralResponse>> create(
            @Valid @RequestBody ReferralRequest request) {
        ReferralResponse created = referralService.createReferral(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Referral created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferralResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Referral retrieved successfully", referralService.getReferralById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReferralResponse>>> search(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long healthRecordId,
            @RequestParam(required = false) ReferralStatus status,
            @RequestParam(required = false) String referredToSpeciality,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<ReferralResponse> results = referralService.searchReferrals(
                employeeId, healthRecordId, status, referredToSpeciality, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Referrals retrieved successfully", results));
    }

    @PutMapping("/{id}/outcome")
    public ResponseEntity<ApiResponse<ReferralResponse>> updateOutcome(
            @PathVariable Long id, @Valid @RequestBody ReferralOutcomeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Referral outcome updated successfully", referralService.updateOutcome(id, request)));
    }
}