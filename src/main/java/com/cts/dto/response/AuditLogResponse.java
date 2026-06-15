package com.cts.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogResponse {

    private Long auditId;
    private Long userId;
    private String action;
    private String entityType;
    private Long recordId;
    private LocalDateTime timestamp;
}