package com.cts.dto.request;

import java.time.LocalDate;

import com.cts.enums.FindingType;
import com.cts.enums.RiskLevel;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to record a finding (Story 18).
 * DueDate requirement for NonConformance is enforced in the service.
 */
@Data
public class FindingRequest {

    @NotNull(message = "ScheduleID is required")
    private Long scheduleId;

    @NotNull(message = "FindingType is required")
    private FindingType findingType;

    private String description;
    private String location;

    @NotNull(message = "RiskLevel is required")
    private RiskLevel riskLevel;

    @NotNull(message = "AssignedToID is required")
    private Long assignedToId;

    private LocalDate dueDate;
}