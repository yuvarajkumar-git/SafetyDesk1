package com.cts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.IncidentInvestigation;
import com.cts.enums.InvestigationStatus;

@Repository
public interface IncidentInvestigationRepository extends JpaRepository<IncidentInvestigation, Long> {

    // renamed: incident is now a relationship -> incident.incidentId
    List<IncidentInvestigation> findByIncident_IncidentId(Long incidentId);

    // renamed: investigator is now a relationship -> investigator.userId
    List<IncidentInvestigation> findByInvestigator_UserId(Long investigatorId);

    List<IncidentInvestigation> findByStatus(InvestigationStatus status);

    List<IncidentInvestigation> findByInvestigationDateBetween(LocalDate from, LocalDate to);
}