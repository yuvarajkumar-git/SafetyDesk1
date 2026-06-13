package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.InspectionRequest;
import com.cts.dto.response.InspectionResponse;
import com.cts.entity.InspectionSchedule;

@Component
public class InspectionMapper {

    public InspectionSchedule toEntity(InspectionRequest request) {
        return InspectionSchedule.builder()
                .siteId(request.getSiteId())
                .inspectionType(request.getInspectionType())
                .assignedOfficerId(request.getAssignedOfficerId())
                .plannedDate(request.getPlannedDate())
                // status set by the service
                .build();
    }

    public InspectionResponse toResponse(InspectionSchedule schedule) {
        return InspectionResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .siteId(schedule.getSiteId())
                .inspectionType(schedule.getInspectionType())
                .assignedOfficerId(schedule.getAssignedOfficerId())
                .plannedDate(schedule.getPlannedDate())
                .status(schedule.getStatus())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}