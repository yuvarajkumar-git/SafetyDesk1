package com.cts.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * Story 14: batch creation of multiple corrective actions for one incident.
 */
@Data
public class CorrectiveActionBatchRequest {

    @NotEmpty(message = "At least one corrective action is required")
    private List<@Valid CorrectiveActionRequest> actions;
}