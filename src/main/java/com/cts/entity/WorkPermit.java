package com.cts.entity;

import java.time.LocalDateTime;

import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;

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
 * WorkPermit (Story 19): a permit authorizing high-risk work in a time window
 * at a specific location.
 */
@Entity
@Table(name = "work_permit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkPermit extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permit_id")
    private Long permitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "permit_type", nullable = false)
    private PermitType permitType;

    @Column(name = "issued_to_id", nullable = false)
    private Long issuedToId;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "work_location", nullable = false)
    private String workLocation;

    @Column(name = "work_description", length = 2000)
    private String workDescription;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "hazards_identified", length = 2000)
    private String hazardsIdentified;

    @Column(name = "control_measures", length = 2000)
    private String controlMeasures;

    @Column(name = "approved_by_id")
    private Long approvedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PermitStatus status;
}