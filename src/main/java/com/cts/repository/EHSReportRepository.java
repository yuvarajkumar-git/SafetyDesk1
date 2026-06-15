package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.EHSReport;

@Repository
public interface EHSReportRepository extends JpaRepository<EHSReport, Long> {
}