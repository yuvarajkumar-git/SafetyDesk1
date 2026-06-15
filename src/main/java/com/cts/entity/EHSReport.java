package com.cts.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.ReportScope;

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
 * EHSReport (Story 23): a generated analytics snapshot. Scope parameters plus
 * the computed metrics (stored as individual columns) and the generation timestamp.
 */
@Entity
@Table(name = "ehs_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EHSReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private ReportScope scope;

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    // --- computed metrics, stored directly (no JSON) ---
    @Column(name = "total_incidents")
    private long totalIncidents;

    @Column(name = "near_miss_count")
    private long nearMissCount;

    @Column(name = "near_miss_frequency_rate")
    private double nearMissFrequencyRate;

    @Column(name = "inspection_completion_rate")
    private double inspectionCompletionRate;

    @Column(name = "corrective_action_closure_rate")
    private double correctiveActionClosureRate;

    @Column(name = "permit_compliance_rate")
    private double permitComplianceRate;

    // Blocked-by-dependency metrics (Story 40/42) stored as nullable
    @Column(name = "ltifr")
    private Double ltifr;

    @Column(name = "trifr")
    private Double trifr;

    @Column(name = "regulatory_obligations_overdue")
    private Long regulatoryObligationsOverdue;

    @Column(name = "generated_date", nullable = false)
    private LocalDateTime generatedDate;
}