package com.cts.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cts.dto.response.AuditLogResponse;

public interface AuditLogService {

    // existing write method (keep your current signature)
    void record(Long userId, String action, String entityType, Long recordId);

    // Story 11 read side
    Page<AuditLogResponse> search(Long userId, String entityType, Long recordId, String action,
                                  LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    // Story 11 export
    String exportCsv(Long userId, String entityType, Long recordId, String action,
                     LocalDateTime fromDate, LocalDateTime toDate);

    String exportHtml(Long userId, String entityType, Long recordId, String action,
                      LocalDateTime fromDate, LocalDateTime toDate);
}