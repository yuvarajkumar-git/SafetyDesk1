package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.PermitRequest;
import com.cts.dto.response.PermitResponse;
import com.cts.entity.WorkPermit;

@Component
public class PermitMapper {

    public WorkPermit toEntity(PermitRequest request) {
        return WorkPermit.builder()
                .permitType(request.getPermitType())
                .issuedToId(request.getIssuedToId())
                .siteId(request.getSiteId())
                .workLocation(request.getWorkLocation())
                .workDescription(request.getWorkDescription())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .hazardsIdentified(request.getHazardsIdentified())
                .controlMeasures(request.getControlMeasures())
                // status set by the service
                .build();
    }

    public PermitResponse toResponse(WorkPermit permit) {
        return PermitResponse.builder()
                .permitId(permit.getPermitId())
                .permitType(permit.getPermitType())
                .issuedToId(permit.getIssuedToId())
                .siteId(permit.getSiteId())
                .workLocation(permit.getWorkLocation())
                .workDescription(permit.getWorkDescription())
                .startDateTime(permit.getStartDateTime())
                .endDateTime(permit.getEndDateTime())
                .hazardsIdentified(permit.getHazardsIdentified())
                .controlMeasures(permit.getControlMeasures())
                .approvedById(permit.getApprovedById())
                .status(permit.getStatus())
                .createdAt(permit.getCreatedAt())
                .updatedAt(permit.getUpdatedAt())
                .build();
    }
}