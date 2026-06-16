package com.cts.entity;

import java.time.LocalDate;

import com.cts.enums.RiskAssessmentStatus;
import com.cts.enums.RiskLevel;

import jakarta.persistence.Column;
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
 * RiskAssessment (Story 16): quantified risk for a hazard.
 * RiskRating (score) and RiskLevel (band) are auto-calculated from
 * Likelihood x Severity in the service, never supplied by the client.
 */
@Entity
@Table(name = "risk_assessment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAssessment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long assessmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hazard_id", nullable = false)
    private HazardRecord hazard;

    @Column(name = "task_description", length = 2000)
    private String taskDescription;

    @Column(name = "likelihood", nullable = false)
    private Integer likelihood; // 1-5

    @Column(name = "severity", nullable = false)
    private Integer severity; // 1-5

    // Numeric score 1-25 (Likelihood x Severity)
    @Column(name = "risk_rating", nullable = false)
    private Integer riskRating;

    // Band derived from riskRating (Low/Medium/High/Critical)
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "existing_controls", length = 2000)
    private String existingControls;

    @Column(name = "additional_controls", length = 2000)
    private String additionalControls;

    @Column(name = "residual_risk", length = 2000)
    private String residualRisk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessed_by_id", nullable = false)
    private User assessedBy;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RiskAssessmentStatus status;
}