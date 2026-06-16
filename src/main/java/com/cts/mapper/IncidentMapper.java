package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.IncidentRequest;
import com.cts.dto.response.IncidentResponse;
import com.cts.entity.IncidentReport;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IncidentMapper {

    private final UserRepository userRepository;

    public IncidentReport toEntity(IncidentRequest request) {
        User reportedBy = userRepository.findById(request.getReportedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getReportedById()));

        return IncidentReport.builder()
                .reportedBy(reportedBy)
                .siteId(request.getSiteId())
                .incidentDate(request.getIncidentDate())
                .incidentType(request.getIncidentType())
                .description(request.getDescription())
                .location(request.getLocation())
                .injuredPersonName(request.getInjuredPersonName())
                .severity(request.getSeverity())
                // status and assignedInvestigator are set by the service
                .build();
    }

    public IncidentResponse toResponse(IncidentReport incident) {
        return IncidentResponse.builder()
                .incidentId(incident.getIncidentId())
                .reportedById(incident.getReportedBy() != null ? incident.getReportedBy().getUserId() : null)
                .siteId(incident.getSiteId())
                .incidentDate(incident.getIncidentDate())
                .incidentType(incident.getIncidentType())
                .description(incident.getDescription())
                .location(incident.getLocation())
                .injuredPersonName(incident.getInjuredPersonName())
                .severity(incident.getSeverity())
                .assignedInvestigatorId(incident.getAssignedInvestigator() != null
                        ? incident.getAssignedInvestigator().getUserId() : null)
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}