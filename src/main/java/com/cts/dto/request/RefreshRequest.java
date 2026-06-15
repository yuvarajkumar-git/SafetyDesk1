package com.cts.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Story 10: exchange a refresh token for a new access token.
 */
@Data
public class RefreshRequest {

    @NotBlank(message = "refreshToken is required")
    private String refreshToken;
}