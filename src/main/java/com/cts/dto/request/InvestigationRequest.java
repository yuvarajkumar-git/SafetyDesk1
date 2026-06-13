package com.cts.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to create/start an investigation (Story 13).
 */
@Data
public class InvestigationRequest {

    @NotNull(message = "IncidentID is required")
    private Long incidentId;

    @NotNull(message = "InvestigatorID is required")
    private Long investigatorId;

    private List<String> rootCauses;
    private List<String> contributingFactors;
    private String immediateActions;
    private String lessonsLearned;

    @NotNull(message = "InvestigationDate is required")
    private LocalDate investigationDate;
}