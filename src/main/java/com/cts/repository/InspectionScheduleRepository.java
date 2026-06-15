package com.cts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.InspectionSchedule;
import com.cts.enums.InspectionStatus;

@Repository
public interface InspectionScheduleRepository
        extends JpaRepository<InspectionSchedule, Long>, JpaSpecificationExecutor<InspectionSchedule> {

    // For auto-Missed detection: scheduled inspections whose planned date has passed
    List<InspectionSchedule> findByStatusAndPlannedDateBefore(InspectionStatus status, LocalDate date);
    java.util.List<com.cts.entity.InspectionSchedule> findByStatusAndPlannedDateBetween(
            com.cts.enums.InspectionStatus status, java.time.LocalDate from, java.time.LocalDate to);
    
    long countByPlannedDateBetween(java.time.LocalDate from, java.time.LocalDate to);
    long countByStatusAndPlannedDateBetween(
            com.cts.enums.InspectionStatus status, java.time.LocalDate from, java.time.LocalDate to);
    
}