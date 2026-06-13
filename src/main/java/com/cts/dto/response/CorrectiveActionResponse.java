package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.CorrectiveActionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CorrectiveActionResponse {

    private Long actionId;
    private Long incidentId;
    private String description;
    private Long assignedToId;
    private LocalDate dueDate;
    private LocalDate closedDate;
    private Long verifiedById;
    private CorrectiveActionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}