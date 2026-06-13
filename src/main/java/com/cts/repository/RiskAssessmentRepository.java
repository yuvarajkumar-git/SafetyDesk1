package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.RiskAssessment;
import com.cts.enums.RiskAssessmentStatus;

@Repository
public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long>, JpaSpecificationExecutor<RiskAssessment> {

    // Used by the "supersede previous assessment" rule
    List<RiskAssessment> findByHazardIdAndStatus(Long hazardId, RiskAssessmentStatus status);
}