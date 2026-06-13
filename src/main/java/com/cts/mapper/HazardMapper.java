package com.cts.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.cts.dto.request.HazardRequest;
import com.cts.dto.response.HazardResponse;
import com.cts.entity.HazardRecord;

@Component
public class HazardMapper {

    public HazardRecord toEntity(HazardRequest request) {
        return HazardRecord.builder()
                .siteId(request.getSiteId())
                .location(request.getLocation())
                .hazardType(request.getHazardType())
                .description(request.getDescription())
                .identifiedById(request.getIdentifiedById())
                // Story 15: default IdentifiedDate to today if not provided
                .identifiedDate(request.getIdentifiedDate() != null
                        ? request.getIdentifiedDate() : LocalDate.now())
                .build();
    }

    public HazardResponse toResponse(HazardRecord hazard) {
        return HazardResponse.builder()
                .hazardId(hazard.getHazardId())
                .siteId(hazard.getSiteId())
                .location(hazard.getLocation())
                .hazardType(hazard.getHazardType())
                .description(hazard.getDescription())
                .identifiedById(hazard.getIdentifiedById())
                .identifiedDate(hazard.getIdentifiedDate())
                .status(hazard.getStatus())
                .createdAt(hazard.getCreatedAt())
                .updatedAt(hazard.getUpdatedAt())
                .build();
    }
}