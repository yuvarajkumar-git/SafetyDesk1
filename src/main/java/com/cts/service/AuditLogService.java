package com.cts.service;

/**
 * Central helper for writing immutable audit log entries (Story 11).
 * Reused by every module that performs safety-critical actions.
 */
public interface AuditLogService {

    void record(Long userId, String action, String entityType, Long recordId);
}