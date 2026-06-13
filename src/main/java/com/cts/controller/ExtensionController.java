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

import com.cts.dto.request.ExtensionDecisionRequest;
import com.cts.dto.request.ExtensionRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.ExtensionResponse;
import com.cts.enums.ExtensionStatus;
import com.cts.service.ExtensionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for PermitExtension (Story 20).
 * Base path: /api/extensions
 */
@Slf4j
@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExtensionResponse>> request(
            @Valid @RequestBody ExtensionRequest request) {
        ExtensionResponse created = extensionService.requestExtension(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Extension requested successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExtensionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Extension retrieved successfully", extensionService.getExtensionById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExtensionResponse>>> search(
            @RequestParam(required = false) Long permitId,
            @RequestParam(required = false) ExtensionStatus status) {
        List<ExtensionResponse> results;
        if (permitId != null) {
            results = extensionService.getByPermitId(permitId);
        } else if (status != null) {
            results = extensionService.getByStatus(status);
        } else {
            results = extensionService.getByStatus(ExtensionStatus.REQUESTED);
        }
        return ResponseEntity.ok(ApiResponse.success("Extensions retrieved successfully", results));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ExtensionResponse>> approve(
            @PathVariable Long id, @Valid @RequestBody ExtensionDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Extension approved successfully", extensionService.approveExtension(id, request)));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ExtensionResponse>> reject(
            @PathVariable Long id, @Valid @RequestBody ExtensionDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Extension rejected successfully", extensionService.rejectExtension(id, request)));
    }
}