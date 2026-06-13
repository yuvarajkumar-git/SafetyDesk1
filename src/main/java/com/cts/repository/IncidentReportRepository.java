package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.IncidentReport;

/**
 * Data access for IncidentReport.
 * JpaSpecificationExecutor enables dynamic filtering (Story 12 query requirements).
 */
@Repository
public interface IncidentReportRepository
        extends JpaRepository<IncidentReport, Long>, JpaSpecificationExecutor<IncidentReport> {
}