package com.cts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.IncidentInvestigation;
import com.cts.enums.InvestigationStatus;

@Repository
public interface IncidentInvestigationRepository extends JpaRepository<IncidentInvestigation, Long> {

    List<IncidentInvestigation> findByIncidentId(Long incidentId);

    List<IncidentInvestigation> findByInvestigatorId(Long investigatorId);

    List<IncidentInvestigation> findByStatus(InvestigationStatus status);

    List<IncidentInvestigation> findByInvestigationDateBetween(LocalDate from, LocalDate to);
}