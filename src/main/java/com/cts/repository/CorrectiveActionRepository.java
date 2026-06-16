package com.cts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.CorrectiveAction;
import com.cts.enums.CorrectiveActionStatus;

@Repository
public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, Long> {

    // renamed: incident is now a relationship -> traverse incident.incidentId
    List<CorrectiveAction> findByIncident_IncidentId(Long incidentId);

    // renamed: assignedTo is now a relationship -> assignedTo.userId
    List<CorrectiveAction> findByAssignedTo_UserId(Long assignedToId);

    List<CorrectiveAction> findByStatus(CorrectiveActionStatus status);

    List<CorrectiveAction> findByDueDateBetween(LocalDate from, LocalDate to);

    long countByStatus(CorrectiveActionStatus status);

    // For the overdue auto-detection: actions past due and not yet completed/verified
    List<CorrectiveAction> findByDueDateBeforeAndStatusNotIn(
            LocalDate date, List<CorrectiveActionStatus> excludedStatuses);
}