package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HazardResponse {

    private Long hazardId;
    private Long siteId;
    private String location;
    private HazardType hazardType;
    private String description;
    private Long identifiedById;
    private LocalDate identifiedDate;
    private HazardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}