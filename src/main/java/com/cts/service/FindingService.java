package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.request.FindingBatchRequest;
import com.cts.dto.request.FindingRequest;
import com.cts.dto.response.FindingResponse;
import com.cts.enums.FindingStatus;
import com.cts.enums.FindingType;
import com.cts.enums.RiskLevel;

public interface FindingService {

    FindingResponse createFinding(FindingRequest request);

    List<FindingResponse> createFindingsBatch(FindingBatchRequest request);

    FindingResponse getFindingById(Long findingId);

    List<FindingResponse> searchFindings(Long scheduleId, FindingType findingType, RiskLevel riskLevel,
                                         FindingStatus status, Long assignedToId,
                                         LocalDate fromDate, LocalDate toDate);

    FindingResponse updateStatus(Long findingId, FindingStatus newStatus);

    int markOverdueFindings();
}