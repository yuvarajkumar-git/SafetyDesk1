package com.cts.dto.request;

import java.time.LocalDate;

import com.cts.enums.IncidentType;
import com.cts.enums.Severity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;

/**
 * Incoming payload to report a new incident (Story 12).
 * reportedById will be auto-populated from the authenticated user later;
 * for now it is accepted in the body.
 */
@Data
public class IncidentRequest {

    @NotNull(message = "ReportedByID is required")
    private Long reportedById;

    @NotNull(message = "SiteID is required")
    private Long siteId;

    @NotNull(message = "IncidentDate is required")
    @PastOrPresent(message = "IncidentDate cannot be in the future")
    private LocalDate incidentDate;

    @NotNull(message = "IncidentType is required")
    private IncidentType incidentType;

    private String description;
    private String location;
    private String injuredPersonName;

    @NotNull(message = "Severity is required")
    private Severity severity;
}