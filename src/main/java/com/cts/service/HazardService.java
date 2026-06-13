package com.cts.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cts.dto.request.HazardRequest;
import com.cts.dto.request.HazardUpdateRequest;
import com.cts.dto.response.HazardResponse;
import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;

public interface HazardService {

    HazardResponse createHazard(HazardRequest request);

    HazardResponse getHazardById(Long hazardId);

    Page<HazardResponse> searchHazards(Long siteId, HazardType hazardType, HazardStatus status,
                                       String location, Long identifiedById,
                                       LocalDate fromDate, LocalDate toDate, Pageable pageable);

    HazardResponse updateHazard(Long hazardId, HazardUpdateRequest request);

    HazardResponse updateStatus(Long hazardId, HazardStatus newStatus);
}