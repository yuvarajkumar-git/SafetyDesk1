package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.FindingRequest;
import com.cts.dto.response.FindingResponse;
import com.cts.entity.InspectionFinding;

@Component
public class FindingMapper {

    public InspectionFinding toEntity(FindingRequest request) {
        return InspectionFinding.builder()
                .scheduleId(request.getScheduleId())
                .findingType(request.getFindingType())
                .description(request.getDescription())
                .location(request.getLocation())
                .riskLevel(request.getRiskLevel())
                .assignedToId(request.getAssignedToId())
                .dueDate(request.getDueDate())
                // status set by the service
                .build();
    }

    public FindingResponse toResponse(InspectionFinding finding) {
        return FindingResponse.builder()
                .findingId(finding.getFindingId())
                .scheduleId(finding.getScheduleId())
                .findingType(finding.getFindingType())
                .description(finding.getDescription())
                .location(finding.getLocation())
                .riskLevel(finding.getRiskLevel())
                .assignedToId(finding.getAssignedToId())
                .dueDate(finding.getDueDate())
                .status(finding.getStatus())
                .createdAt(finding.getCreatedAt())
                .updatedAt(finding.getUpdatedAt())
                .build();
    }
}