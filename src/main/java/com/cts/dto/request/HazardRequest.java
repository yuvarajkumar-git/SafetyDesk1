package com.cts.dto.request;

import java.time.LocalDate;

import com.cts.enums.HazardType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;

/**
 * Payload to record a hazard (Story 15).
 * identifiedById will be auto-populated from the authenticated user later;
 * for now it is accepted in the body.
 */
@Data
public class HazardRequest {

    @NotNull(message = "SiteID is required")
    private Long siteId;

    private String location;

    @NotNull(message = "HazardType is required")
    private HazardType hazardType;

    private String description;

    @NotNull(message = "IdentifiedByID is required")
    private Long identifiedById;

    // Optional: defaults to today in the service if omitted (Story 15).
    @PastOrPresent(message = "IdentifiedDate cannot be in the future")
    private LocalDate identifiedDate;
}