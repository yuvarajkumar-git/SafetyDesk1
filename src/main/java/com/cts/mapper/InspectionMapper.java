package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.InspectionRequest;
import com.cts.dto.response.InspectionResponse;
import com.cts.entity.InspectionSchedule;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InspectionMapper {

    private final UserRepository userRepository;

    public InspectionSchedule toEntity(InspectionRequest request) {
        User assignedOfficer = userRepository.findById(request.getAssignedOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assigned officer (User) not found with id: " + request.getAssignedOfficerId()));

        return InspectionSchedule.builder()
                .siteId(request.getSiteId())
                .inspectionType(request.getInspectionType())
                .assignedOfficer(assignedOfficer)
                .plannedDate(request.getPlannedDate())
                // status set by the service
                .build();
    }

    public InspectionResponse toResponse(InspectionSchedule schedule) {
        return InspectionResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .siteId(schedule.getSiteId())
                .inspectionType(schedule.getInspectionType())
                .assignedOfficerId(schedule.getAssignedOfficer() != null
                        ? schedule.getAssignedOfficer().getUserId() : null)
                .plannedDate(schedule.getPlannedDate())
                .status(schedule.getStatus())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}