package com.cts.dto.request;

import java.time.LocalDate;

import com.cts.enums.InspectionType;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to schedule an inspection (Story 17).
 * The future-date rule (and the IncidentFollow-Up exception) is enforced
 * in the service, since it depends on the inspection type.
 */
@Data
public class InspectionRequest {

    @NotNull(message = "SiteID is required")
    private Long siteId;

    @NotNull(message = "InspectionType is required")
    private InspectionType inspectionType;

    @NotNull(message = "AssignedOfficerID is required")
    private Long assignedOfficerId;

    @NotNull(message = "PlannedDate is required")
    private LocalDate plannedDate;
}