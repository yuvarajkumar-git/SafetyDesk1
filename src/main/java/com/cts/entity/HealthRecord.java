package com.cts.entity;

import java.time.LocalDate;

import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HealthRecordStatus;

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
 * HealthRecord (Story 21): an occupational health surveillance assessment.
 */
@Entity
@Table(name = "health_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecord extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_record_id")
    private Long healthRecordId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false)
    private AssessmentType assessmentType;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Column(name = "conducted_by_id", nullable = false)
    private Long conductedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "fitness_decision", nullable = false)
    private FitnessDecision fitnessDecision;

    @Column(name = "next_assessment_date")
    private LocalDate nextAssessmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HealthRecordStatus status;
}