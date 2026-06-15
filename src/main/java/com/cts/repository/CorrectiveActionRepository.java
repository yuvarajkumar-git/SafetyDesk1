package com.cts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.CorrectiveAction;
import com.cts.enums.CorrectiveActionStatus;

@Repository
public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, Long> {

    List<CorrectiveAction> findByIncidentId(Long incidentId);

    List<CorrectiveAction> findByAssignedToId(Long assignedToId);

    List<CorrectiveAction> findByStatus(CorrectiveActionStatus status);

    List<CorrectiveAction> findByDueDateBetween(LocalDate from, LocalDate to);
    
    long countByStatus(com.cts.enums.CorrectiveActionStatus status);
    long count();   // inherited from JpaRepository; no need to add

    // For the overdue auto-detection: actions past due and not yet completed/verified
    List<CorrectiveAction> findByDueDateBeforeAndStatusNotIn(
            LocalDate date, List<CorrectiveActionStatus> excludedStatuses);
}