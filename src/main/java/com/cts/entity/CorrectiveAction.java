package com.cts.entity;

import java.time.LocalDate;

import com.cts.enums.CorrectiveActionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * CorrectiveAction (Story 14): a CAPA item linked to an incident.
 */
@Entity
@Table(name = "corrective_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrectiveAction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    @Column(name = "incident_id", nullable = false)
    private Long incidentId;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @Column(name = "assigned_to_id", nullable = false)
    private Long assignedToId;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    @Column(name = "verified_by_id")
    private Long verifiedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CorrectiveActionStatus status;
}