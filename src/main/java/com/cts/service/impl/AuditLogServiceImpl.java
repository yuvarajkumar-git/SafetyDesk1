package com.cts.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.response.AuditLogResponse;
import com.cts.entity.AuditLog;
import com.cts.repository.AuditLogRepository;
import com.cts.repository.spec.AuditLogSpecification;
import com.cts.service.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // ===== WRITE (existing) =====
    @Override
    @Transactional
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

    // ===== READ (Story 11) =====
    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> search(Long userId, String entityType, Long recordId, String action,
                                         LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        var spec = AuditLogSpecification.build(userId, entityType, recordId, action, fromDate, toDate);
        return auditLogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    // ===== EXPORT: CSV (Story 11) =====
    @Override
    @Transactional(readOnly = true)
    public String exportCsv(Long userId, String entityType, Long recordId, String action,
                            LocalDateTime fromDate, LocalDateTime toDate) {
        var spec = AuditLogSpecification.build(userId, entityType, recordId, action, fromDate, toDate);
        List<AuditLog> rows = auditLogRepository.findAll(spec);

        StringBuilder sb = new StringBuilder();
        sb.append("AuditID,UserID,Action,EntityType,RecordID,Timestamp\n");
        for (AuditLog a : rows) {
            sb.append(a.getAuditId()).append(',')
              .append(a.getUserId()).append(',')
              .append(csv(a.getAction())).append(',')
              .append(csv(a.getEntityType())).append(',')
              .append(a.getRecordId()).append(',')
              .append(a.getTimestamp()).append('\n');
        }
        return sb.toString();
    }

    // ===== EXPORT: print-ready HTML (browser -> Save as PDF) =====
    @Override
    @Transactional(readOnly = true)
    public String exportHtml(Long userId, String entityType, Long recordId, String action,
                             LocalDateTime fromDate, LocalDateTime toDate) {
        var spec = AuditLogSpecification.build(userId, entityType, recordId, action, fromDate, toDate);
        List<AuditLog> rows = auditLogRepository.findAll(spec);

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
          .append("<title>SafetyDesk Audit Report</title>")
          .append("<style>body{font-family:Arial,sans-serif;margin:24px}")
          .append("h1{font-size:18px}table{border-collapse:collapse;width:100%;font-size:12px}")
          .append("th,td{border:1px solid #999;padding:4px 8px;text-align:left}")
          .append("th{background:#eee}</style></head><body>");
        sb.append("<h1>SafetyDesk Audit Report</h1>");
        sb.append("<p>Generated: ").append(LocalDateTime.now()).append(" &middot; ")
          .append(rows.size()).append(" entries</p>");
        sb.append("<table><tr><th>AuditID</th><th>UserID</th><th>Action</th>")
          .append("<th>EntityType</th><th>RecordID</th><th>Timestamp</th></tr>");
        for (AuditLog a : rows) {
            sb.append("<tr><td>").append(a.getAuditId()).append("</td><td>")
              .append(a.getUserId()).append("</td><td>")
              .append(html(a.getAction())).append("</td><td>")
              .append(html(a.getEntityType())).append("</td><td>")
              .append(a.getRecordId()).append("</td><td>")
              .append(a.getTimestamp()).append("</td></tr>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    // --- helpers ---

    private AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .auditId(a.getAuditId())
                .userId(a.getUserId())
                .action(a.getAction())
                .entityType(a.getEntityType())
                .recordId(a.getRecordId())
                .timestamp(a.getTimestamp())
                .build();
    }

    // CSV-escape: wrap in quotes if it contains a comma/quote/newline
    private String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // minimal HTML-escape
    private String html(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}