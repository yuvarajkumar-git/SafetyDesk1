package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.InspectionStatus;
import com.cts.enums.InspectionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InspectionResponse {

    private Long scheduleId;
    private Long siteId;
    private InspectionType inspectionType;
    private Long assignedOfficerId;
    private LocalDate plannedDate;
    private InspectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}