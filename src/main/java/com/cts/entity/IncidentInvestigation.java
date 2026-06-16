package com.cts.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cts.enums.InvestigationStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IncidentInvestigation (Story 13): investigation findings linked to an incident.
 */
@Entity
@Table(name = "incident_investigation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentInvestigation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investigation_id")
    private Long investigationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private IncidentReport incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigator_id", nullable = false)
    private User investigator;

    // Multi-value: stored in a side-table "investigation_root_causes".
    // EAGER so the list is always loaded with the entity.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "investigation_root_causes",
            joinColumns = @JoinColumn(name = "investigation_id"))
    @Column(name = "root_cause", length = 1000)
    @Builder.Default
    private List<String> rootCauses = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "investigation_contributing_factors",
            joinColumns = @JoinColumn(name = "investigation_id"))
    @Column(name = "contributing_factor", length = 1000)
    @Builder.Default
    private List<String> contributingFactors = new ArrayList<>();

    @Column(name = "immediate_actions", length = 2000)
    private String immediateActions;

    @Column(name = "lessons_learned", length = 2000)
    private String lessonsLearned;

    @Column(name = "investigation_date", nullable = false)
    private LocalDate investigationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvestigationStatus status;
}