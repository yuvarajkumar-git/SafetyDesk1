package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.AuditLog;

/**
 * Data access for AuditLog. Read + insert only —
 * we never call update or delete (immutability per Story 11).
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}