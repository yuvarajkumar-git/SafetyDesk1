package com.cts.service;

import java.util.List;

import com.cts.dto.request.CorrectiveActionBatchRequest;
import com.cts.dto.request.CorrectiveActionRequest;
import com.cts.dto.response.CorrectiveActionResponse;
import com.cts.enums.CorrectiveActionStatus;

public interface CorrectiveActionService {

    CorrectiveActionResponse createAction(CorrectiveActionRequest request);

    List<CorrectiveActionResponse> createActionsBatch(CorrectiveActionBatchRequest request);

    CorrectiveActionResponse getActionById(Long actionId);

    List<CorrectiveActionResponse> getByIncidentId(Long incidentId);

    List<CorrectiveActionResponse> getByStatus(CorrectiveActionStatus status);

    CorrectiveActionResponse updateStatus(Long actionId, CorrectiveActionStatus newStatus, Long verifiedById);

    int markOverdueActions(); // returns how many were flagged
}