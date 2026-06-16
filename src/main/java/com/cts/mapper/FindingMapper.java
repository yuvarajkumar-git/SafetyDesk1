package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.FindingRequest;
import com.cts.dto.response.FindingResponse;
import com.cts.entity.InspectionFinding;
import com.cts.entity.InspectionSchedule;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.InspectionScheduleRepository;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindingMapper {

    private final InspectionScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public InspectionFinding toEntity(FindingRequest request) {
        InspectionSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inspection schedule not found with id: " + request.getScheduleId()));
        User assignedTo = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assignee (User) not found with id: " + request.getAssignedToId()));

        return InspectionFinding.builder()
                .schedule(schedule)
                .findingType(request.getFindingType())
                .description(request.getDescription())
                .location(request.getLocation())
                .riskLevel(request.getRiskLevel())
                .assignedTo(assignedTo)
                .dueDate(request.getDueDate())
                // status set by the service
                .build();
    }

    public FindingResponse toResponse(InspectionFinding finding) {
        return FindingResponse.builder()
                .findingId(finding.getFindingId())
                .scheduleId(finding.getSchedule() != null ? finding.getSchedule().getScheduleId() : null)
                .findingType(finding.getFindingType())
                .description(finding.getDescription())
                .location(finding.getLocation())
                .riskLevel(finding.getRiskLevel())
                .assignedToId(finding.getAssignedTo() != null ? finding.getAssignedTo().getUserId() : null)
                .dueDate(finding.getDueDate())
                .status(finding.getStatus())
                .createdAt(finding.getCreatedAt())
                .updatedAt(finding.getUpdatedAt())
                .build();
    }
}