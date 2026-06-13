package com.cts.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload for assigning an investigator and moving the incident
 * from Reported -> UnderInvestigation (Story 12).
 */
@Data
public class InvestigatorAssignmentRequest {

    @NotNull(message = "AssignedInvestigatorID is required")
    private Long assignedInvestigatorId;
}