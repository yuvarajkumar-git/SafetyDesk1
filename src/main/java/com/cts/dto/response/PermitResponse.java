package com.cts.dto.response;

import java.time.LocalDateTime;

import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermitResponse {

    private Long permitId;
    private PermitType permitType;
    private Long issuedToId;
    private Long siteId;
    private String workLocation;
    private String workDescription;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String hazardsIdentified;
    private String controlMeasures;
    private Long approvedById;
    private PermitStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}