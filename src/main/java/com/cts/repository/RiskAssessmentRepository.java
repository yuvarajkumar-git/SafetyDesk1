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

    // renamed: hazard is now a relationship -> hazard.hazardId
    List<RiskAssessment> findByHazard_HazardIdAndStatus(Long hazardId, RiskAssessmentStatus status);
}