package com.cts.entity;

import java.time.LocalDate;

import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;

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
 * HazardRecord (Story 15): an identified workplace hazard in the site hazard register.
 */
@Entity
@Table(name = "hazard_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HazardRecord extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hazard_id")
    private Long hazardId;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "hazard_type", nullable = false)
    private HazardType hazardType;

    @Column(name = "description", length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identified_by_id", nullable = false)
    private User identifiedBy;

    @Column(name = "identified_date", nullable = false)
    private LocalDate identifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HazardStatus status;
}