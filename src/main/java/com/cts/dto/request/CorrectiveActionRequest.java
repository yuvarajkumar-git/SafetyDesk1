package com.cts.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to create a corrective action (Story 14).
 */
@Data
public class CorrectiveActionRequest {

    @NotNull(message = "IncidentID is required")
    private Long incidentId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "AssignedToID is required")
    private Long assignedToId;

    @NotNull(message = "DueDate is required")
    @Future(message = "DueDate must be in the future")
    private LocalDate dueDate;
}