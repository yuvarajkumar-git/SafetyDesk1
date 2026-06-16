package com.cts.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.cts.dto.request.HazardRequest;
import com.cts.dto.response.HazardResponse;
import com.cts.entity.HazardRecord;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HazardMapper {

    private final UserRepository userRepository;

    public HazardRecord toEntity(HazardRequest request) {
        User identifiedBy = userRepository.findById(request.getIdentifiedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getIdentifiedById()));

        return HazardRecord.builder()
                .siteId(request.getSiteId())
                .location(request.getLocation())
                .hazardType(request.getHazardType())
                .description(request.getDescription())
                .identifiedBy(identifiedBy)
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
                .identifiedById(hazard.getIdentifiedBy() != null ? hazard.getIdentifiedBy().getUserId() : null)
                .identifiedDate(hazard.getIdentifiedDate())
                .status(hazard.getStatus())
                .createdAt(hazard.getCreatedAt())
                .updatedAt(hazard.getUpdatedAt())
                .build();
    }
}