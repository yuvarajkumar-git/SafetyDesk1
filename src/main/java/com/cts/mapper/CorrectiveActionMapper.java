package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.CorrectiveActionRequest;
import com.cts.dto.response.CorrectiveActionResponse;
import com.cts.entity.CorrectiveAction;
import com.cts.entity.IncidentReport;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.IncidentReportRepository;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CorrectiveActionMapper {

    private final IncidentReportRepository incidentRepository;
    private final UserRepository userRepository;

    public CorrectiveAction toEntity(CorrectiveActionRequest request) {
        IncidentReport incident = incidentRepository.findById(request.getIncidentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Incident not found with id: " + request.getIncidentId()));
        User assignedTo = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignee (User) not found with id: " + request.getAssignedToId()));

        return CorrectiveAction.builder()
                .incident(incident)
                .description(request.getDescription())
                .assignedTo(assignedTo)
                .dueDate(request.getDueDate())
                // verifiedBy and status are set by the service
                .build();
    }

    public CorrectiveActionResponse toResponse(CorrectiveAction action) {
        return CorrectiveActionResponse.builder()
                .actionId(action.getActionId())
                .incidentId(action.getIncident() != null ? action.getIncident().getIncidentId() : null)
                .description(action.getDescription())
                .assignedToId(action.getAssignedTo() != null ? action.getAssignedTo().getUserId() : null)
                .dueDate(action.getDueDate())
                .closedDate(action.getClosedDate())
                .verifiedById(action.getVerifiedBy() != null ? action.getVerifiedBy().getUserId() : null)
                .status(action.getStatus())
                .createdAt(action.getCreatedAt())
                .updatedAt(action.getUpdatedAt())
                .build();
    }
}