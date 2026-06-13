package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.CorrectiveActionRequest;
import com.cts.dto.response.CorrectiveActionResponse;
import com.cts.entity.CorrectiveAction;

@Component
public class CorrectiveActionMapper {

    public CorrectiveAction toEntity(CorrectiveActionRequest request) {
        return CorrectiveAction.builder()
                .incidentId(request.getIncidentId())
                .description(request.getDescription())
                .assignedToId(request.getAssignedToId())
                .dueDate(request.getDueDate())
                .build();
    }

    public CorrectiveActionResponse toResponse(CorrectiveAction action) {
        return CorrectiveActionResponse.builder()
                .actionId(action.getActionId())
                .incidentId(action.getIncidentId())
                .description(action.getDescription())
                .assignedToId(action.getAssignedToId())
                .dueDate(action.getDueDate())
                .closedDate(action.getClosedDate())
                .verifiedById(action.getVerifiedById())
                .status(action.getStatus())
                .createdAt(action.getCreatedAt())
                .updatedAt(action.getUpdatedAt())
                .build();
    }
}