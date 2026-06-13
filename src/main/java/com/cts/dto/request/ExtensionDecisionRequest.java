package com.cts.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to approve or reject an extension (Story 20).
 */
@Data
public class ExtensionDecisionRequest {

    @NotNull(message = "ApprovedByID is required")
    private Long approvedById;
}