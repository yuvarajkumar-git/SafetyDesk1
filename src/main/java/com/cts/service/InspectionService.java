package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.request.InspectionRequest;
import com.cts.dto.request.RecurringInspectionRequest;
import com.cts.dto.response.InspectionResponse;
import com.cts.enums.InspectionStatus;
import com.cts.enums.InspectionType;

public interface InspectionService {

    InspectionResponse scheduleInspection(InspectionRequest request);

    List<InspectionResponse> scheduleRecurring(RecurringInspectionRequest request);

    InspectionResponse getInspectionById(Long scheduleId);

    List<InspectionResponse> searchInspections(Long siteId, InspectionType inspectionType,
                                               Long assignedOfficerId, InspectionStatus status,
                                               LocalDate fromDate, LocalDate toDate);

    InspectionResponse updateStatus(Long scheduleId, InspectionStatus newStatus);

    int markMissedInspections();
}