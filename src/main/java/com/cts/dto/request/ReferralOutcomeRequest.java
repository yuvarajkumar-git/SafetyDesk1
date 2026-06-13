package com.cts.dto.request;

import com.cts.enums.ReferralStatus;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to update a referral's outcome / status (Story 22).
 */
@Data
public class ReferralOutcomeRequest {

    @NotNull(message = "Status is required")
    private ReferralStatus status;

    // Required when transitioning to Attended or Closed (enforced in service)
    private String outcomeSummary;
}