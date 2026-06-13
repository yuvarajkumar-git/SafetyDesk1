package com.cts.dto.request;

import lombok.Data;

/**
 * Payload to fill in / update a Draft permit's details (Story 19),
 * notably the mandatory-before-approval fields. All optional.
 */
@Data
public class PermitUpdateRequest {

    private String workDescription;
    private String hazardsIdentified;
    private String controlMeasures;
}