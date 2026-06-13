package com.cts.dto.request;

import java.util.List;

import lombok.Data;

/**
 * Payload to update investigation findings (Story 13). All fields optional.
 */
@Data
public class InvestigationUpdateRequest {

    private List<String> rootCauses;
    private List<String> contributingFactors;
    private String immediateActions;
    private String lessonsLearned;
}