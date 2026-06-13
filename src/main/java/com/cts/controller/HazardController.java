package com.cts.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.cts.dto.request.HazardRequest;
import com.cts.dto.request.HazardUpdateRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.HazardResponse;
import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;
import com.cts.service.HazardService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for HazardRecord (Story 15).
 * Base path: /api/hazards
 */
@Slf4j
@RestController
@RequestMapping("/api/hazards")
@RequiredArgsConstructor
public class HazardController {

    private final HazardService hazardService;

    @PostMapping
    public ResponseEntity<ApiResponse<HazardResponse>> create(
            @Valid @RequestBody HazardRequest request) {
        HazardResponse created = hazardService.createHazard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hazard recorded successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HazardResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hazard retrieved successfully", hazardService.getHazardById(id)));
    }

    // Paged + filtered list (Story 15). Default sort: IdentifiedDate descending.
    @GetMapping
    public ResponseEntity<ApiResponse<Page<HazardResponse>>> search(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) HazardType hazardType,
            @RequestParam(required = false) HazardStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long identifiedById,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/hazards (search, paged)");
        Pageable pageable = PageRequest.of(page, size, Sort.by("identifiedDate").descending());
        Page<HazardResponse> results = hazardService.searchHazards(
                siteId, hazardType, status, location, identifiedById, fromDate, toDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("Hazards retrieved successfully", results));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HazardResponse>> update(
            @PathVariable Long id, @Valid @RequestBody HazardUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hazard updated successfully", hazardService.updateHazard(id, request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<HazardResponse>> updateStatus(
            @PathVariable Long id, @RequestParam HazardStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hazard status updated successfully", hazardService.updateStatus(id, status)));
    }
}