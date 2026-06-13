package com.cts.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AuditLog (Story 11): immutable record of safety-critical actions.
 * Fields per story: AuditID, UserID, Action, EntityType, RecordID, Timestamp.
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    // The user who performed the action
    @Column(name = "user_id")
    private Long userId;

    // What happened, e.g. "CREATE_USER", "Login", "FailedLogin"
    @Column(name = "action", nullable = false)
    private String action;

    // Which entity type the action targeted, e.g. "User", "IncidentReport"
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    // The primary key of the affected record
    @Column(name = "record_id")
    private Long recordId;

    // When it happened
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}