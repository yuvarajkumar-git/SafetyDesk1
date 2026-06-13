package com.cts.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cts.entity.WorkPermit;
import com.cts.enums.PermitStatus;

@Repository
public interface WorkPermitRepository
        extends JpaRepository<WorkPermit, Long>, JpaSpecificationExecutor<WorkPermit> {

    /**
     * Conflict detection (Story 19): find ACTIVE permits at the same location
     * whose time window overlaps [start, end]. Overlap rule:
     *   existing.start < newEnd AND existing.end > newStart
     * The excludePermitId lets us ignore the permit being checked itself
     * (important when re-checking during extension). Pass a non-existent id
     * (e.g. -1) when creating a brand-new permit.
     */
    @Query("""
            SELECT p FROM WorkPermit p
            WHERE p.workLocation = :location
              AND p.status = :activeStatus
              AND p.permitId <> :excludePermitId
              AND p.startDateTime < :newEnd
              AND p.endDateTime > :newStart
            """)
    List<WorkPermit> findConflictingPermits(
            @Param("location") String location,
            @Param("activeStatus") PermitStatus activeStatus,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd,
            @Param("excludePermitId") Long excludePermitId);

    // For auto-Expired detection: active permits whose end time has passed
    List<WorkPermit> findByStatusAndEndDateTimeBefore(PermitStatus status, LocalDateTime dateTime);
}