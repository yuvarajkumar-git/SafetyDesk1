package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.IncidentRequest;
import com.cts.dto.response.IncidentResponse;
import com.cts.entity.IncidentReport;

@Component
public class IncidentMapper {

    public IncidentReport toEntity(IncidentRequest request) {
        return IncidentReport.builder()
                .reportedById(request.getReportedById())
                .siteId(request.getSiteId())
                .incidentDate(request.getIncidentDate())
                .incidentType(request.getIncidentType())
                .description(request.getDescription())
                .location(request.getLocation())
                .injuredPersonName(request.getInjuredPersonName())
                .severity(request.getSeverity())
                // status is set by the service, not the client
                .build();
    }

    public IncidentResponse toResponse(IncidentReport incident) {
        return IncidentResponse.builder()
                .incidentId(incident.getIncidentId())
                .reportedById(incident.getReportedById())
                .siteId(incident.getSiteId())
                .incidentDate(incident.getIncidentDate())
                .incidentType(incident.getIncidentType())
                .description(incident.getDescription())
                .location(incident.getLocation())
                .injuredPersonName(incident.getInjuredPersonName())
                .severity(incident.getSeverity())
                .assignedInvestigatorId(incident.getAssignedInvestigatorId())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}