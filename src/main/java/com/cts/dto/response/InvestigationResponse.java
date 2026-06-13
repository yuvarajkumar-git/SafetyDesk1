package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.cts.enums.InvestigationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvestigationResponse {

    private Long investigationId;
    private Long incidentId;
    private Long investigatorId;
    private List<String> rootCauses;
    private List<String> contributingFactors;
    private String immediateActions;
    private String lessonsLearned;
    private LocalDate investigationDate;
    private InvestigationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}