package com.cts.dto.request;

import java.time.LocalDateTime;

import com.cts.enums.PermitType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to create a work permit in Draft (Story 19).
 * HazardsIdentified / ControlMeasures are optional at draft time but become
 * mandatory before submitting for approval (enforced in the service).
 */
@Data
public class PermitRequest {

    @NotNull(message = "PermitType is required")
    private PermitType permitType;

    @NotNull(message = "IssuedToID is required")
    private Long issuedToId;

    @NotNull(message = "SiteID is required")
    private Long siteId;

    @NotBlank(message = "WorkLocation is required")
    private String workLocation;

    private String workDescription;

    @NotNull(message = "StartDateTime is required")
    @Future(message = "StartDateTime must be in the future")
    private LocalDateTime startDateTime;

    @NotNull(message = "EndDateTime is required")
    @Future(message = "EndDateTime must be in the future")
    private LocalDateTime endDateTime;

    private String hazardsIdentified;
    private String controlMeasures;
}