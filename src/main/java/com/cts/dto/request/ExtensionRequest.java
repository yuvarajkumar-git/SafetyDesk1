package com.cts.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Payload to request a permit extension (Story 20).
 */
@Data
public class ExtensionRequest {

    @NotNull(message = "PermitID is required")
    private Long permitId;

    @NotNull(message = "RequestedByID is required")
    private Long requestedById;

    @NotNull(message = "NewEndDateTime is required")
    @Future(message = "NewEndDateTime must be in the future")
    private LocalDateTime newEndDateTime;

    @NotBlank(message = "Reason is required")
    private String reason;
}