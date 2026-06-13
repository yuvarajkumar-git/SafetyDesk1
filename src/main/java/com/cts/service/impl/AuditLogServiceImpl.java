package com.cts.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.cts.entity.AuditLog;
import com.cts.repository.AuditLogRepository;
import com.cts.service.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void record(Long userId, String action, String entityType, Long recordId) {
        AuditLog entry = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .recordId(recordId)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(entry);
        log.info("Audit recorded: action={}, entityType={}, recordId={}, byUser={}",
                action, entityType, recordId, userId);
    }
}