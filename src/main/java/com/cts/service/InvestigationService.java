package com.cts.service;

import java.util.List;

import com.cts.dto.request.InvestigationRequest;
import com.cts.dto.request.InvestigationUpdateRequest;
import com.cts.dto.response.InvestigationResponse;
import com.cts.enums.InvestigationStatus;

public interface InvestigationService {

    InvestigationResponse createInvestigation(InvestigationRequest request);

    InvestigationResponse getInvestigationById(Long investigationId);

    List<InvestigationResponse> getByIncidentId(Long incidentId);

    List<InvestigationResponse> getByStatus(InvestigationStatus status);

    List<InvestigationResponse> getAll();

    InvestigationResponse updateFindings(Long investigationId, InvestigationUpdateRequest request);

    InvestigationResponse updateStatus(Long investigationId, InvestigationStatus newStatus,
                                       boolean correctiveActionsNeeded);
}