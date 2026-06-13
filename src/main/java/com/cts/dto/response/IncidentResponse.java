package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.Severity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncidentResponse {

    private Long incidentId;
    private Long reportedById;
    private Long siteId;
    private LocalDate incidentDate;
    private IncidentType incidentType;
    private String description;
    private String location;
    private String injuredPersonName;
    private Severity severity;
    private Long assignedInvestigatorId;
    private IncidentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}