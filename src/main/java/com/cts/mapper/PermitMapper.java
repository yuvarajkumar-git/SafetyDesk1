package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.PermitRequest;
import com.cts.dto.response.PermitResponse;
import com.cts.entity.User;
import com.cts.entity.WorkPermit;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitMapper {

    private final UserRepository userRepository;

    public WorkPermit toEntity(PermitRequest request) {
        User issuedTo = userRepository.findById(request.getIssuedToId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "IssuedTo (User) not found with id: " + request.getIssuedToId()));

        return WorkPermit.builder()
                .permitType(request.getPermitType())
                .issuedTo(issuedTo)
                .siteId(request.getSiteId())
                .workLocation(request.getWorkLocation())
                .workDescription(request.getWorkDescription())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .hazardsIdentified(request.getHazardsIdentified())
                .controlMeasures(request.getControlMeasures())
                // approvedBy and status are set by the service
                .build();
    }

    public PermitResponse toResponse(WorkPermit permit) {
        return PermitResponse.builder()
                .permitId(permit.getPermitId())
                .permitType(permit.getPermitType())
                .issuedToId(permit.getIssuedTo() != null ? permit.getIssuedTo().getUserId() : null)
                .siteId(permit.getSiteId())
                .workLocation(permit.getWorkLocation())
                .workDescription(permit.getWorkDescription())
                .startDateTime(permit.getStartDateTime())
                .endDateTime(permit.getEndDateTime())
                .hazardsIdentified(permit.getHazardsIdentified())
                .controlMeasures(permit.getControlMeasures())
                .approvedById(permit.getApprovedBy() != null ? permit.getApprovedBy().getUserId() : null)
                .status(permit.getStatus())
                .createdAt(permit.getCreatedAt())
                .updatedAt(permit.getUpdatedAt())
                .build();
    }
}