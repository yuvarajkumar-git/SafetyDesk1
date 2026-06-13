package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.FindingStatus;
import com.cts.enums.FindingType;
import com.cts.enums.RiskLevel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindingResponse {

    private Long findingId;
    private Long scheduleId;
    private FindingType findingType;
    private String description;
    private String location;
    private RiskLevel riskLevel;
    private Long assignedToId;
    private LocalDate dueDate;
    private FindingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}