package com.cts.dto.request;

import com.cts.enums.HazardType;

import lombok.Data;

/**
 * Payload to update a hazard (Story 15). All fields optional.
 * Status changes go through the dedicated status endpoint.
 */
@Data
public class HazardUpdateRequest {

    private String location;
    private HazardType hazardType;
    private String description;
}