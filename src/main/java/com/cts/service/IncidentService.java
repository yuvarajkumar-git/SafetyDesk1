package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.request.IncidentRequest;
import com.cts.dto.request.InvestigatorAssignmentRequest;
import com.cts.dto.response.IncidentResponse;
import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.Severity;

public interface IncidentService {

    IncidentResponse createIncident(IncidentRequest request);

    IncidentResponse getIncidentById(Long incidentId);

    List<IncidentResponse> searchIncidents(Long siteId, IncidentType type, Severity severity,
                                           IncidentStatus status, Long reportedById,
                                           Long assignedInvestigatorId,
                                           LocalDate fromDate, LocalDate toDate);

    IncidentResponse assignInvestigator(Long incidentId, InvestigatorAssignmentRequest request);

    IncidentResponse updateStatus(Long incidentId, IncidentStatus newStatus);
}