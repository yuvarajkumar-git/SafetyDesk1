package com.cts.dto.request;

import java.time.LocalDate;

import com.cts.enums.InspectionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload for recurring inspection scheduling (Story 17),
 * e.g. weekly Routine inspections.
 */
@Data
public class RecurringInspectionRequest {

    @NotNull(message = "SiteID is required")
    private Long siteId;

    @NotNull(message = "InspectionType is required")
    private InspectionType inspectionType;

    @NotNull(message = "AssignedOfficerID is required")
    private Long assignedOfficerId;

    @NotNull(message = "StartDate is required")
    private LocalDate startDate;

    // How many days between each occurrence (e.g. 7 for weekly)
    @NotNull(message = "IntervalDays is required")
    @Min(value = 1, message = "IntervalDays must be at least 1")
    private Integer intervalDays;

    // How many occurrences to generate
    @NotNull(message = "Occurrences is required")
    @Min(value = 1, message = "Occurrences must be at least 1")
    private Integer occurrences;
}