package com.cts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.InspectionFinding;
import com.cts.enums.FindingStatus;

@Repository
public interface InspectionFindingRepository
        extends JpaRepository<InspectionFinding, Long>, JpaSpecificationExecutor<InspectionFinding> {

    // For overdue detection: findings past due and not yet closed
    List<InspectionFinding> findByDueDateBeforeAndStatusNotIn(LocalDate date, List<FindingStatus> excluded);
}