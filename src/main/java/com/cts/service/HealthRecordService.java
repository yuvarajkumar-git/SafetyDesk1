package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.request.HealthRecordRequest;
import com.cts.dto.response.HealthRecordResponse;
import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HealthRecordStatus;

public interface HealthRecordService {

    HealthRecordResponse createHealthRecord(HealthRecordRequest request);

    HealthRecordResponse getHealthRecordById(Long healthRecordId);

    List<HealthRecordResponse> searchHealthRecords(Long employeeId, AssessmentType assessmentType,
                                                   FitnessDecision fitnessDecision, HealthRecordStatus status,
                                                   Long conductedById,
                                                   LocalDate assessmentFrom, LocalDate assessmentTo,
                                                   LocalDate nextFrom, LocalDate nextTo);

    HealthRecordResponse updateStatus(Long healthRecordId, HealthRecordStatus status);

    int remindUpcomingAssessments(int withinDays);
}