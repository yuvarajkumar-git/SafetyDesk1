package com.cts.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to approve a permit (Story 19): who is approving.
 */
@Data
public class PermitApprovalRequest {

    @NotNull(message = "ApprovedByID is required")
    private Long approvedById;
}