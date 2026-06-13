package com.cts.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * Story 18: create multiple findings for a single inspection schedule.
 */
@Data
public class FindingBatchRequest {

    @NotEmpty(message = "At least one finding is required")
    private List<@Valid FindingRequest> findings;
}