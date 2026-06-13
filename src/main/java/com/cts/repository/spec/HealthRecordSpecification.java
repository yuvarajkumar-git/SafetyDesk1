package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.HealthRecord;
import com.cts.enums.AssessmentType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HealthRecordStatus;

/**
 * Builds dynamic WHERE conditions for health record queries (Story 21).
 */
public final class HealthRecordSpecification {

    private HealthRecordSpecification() { }

    public static Specification<HealthRecord> build(
            Long employeeId, AssessmentType assessmentType, FitnessDecision fitnessDecision,
            HealthRecordStatus status, Long conductedById,
            LocalDate assessmentFrom, LocalDate assessmentTo,
            LocalDate nextFrom, LocalDate nextTo) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (employeeId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("employeeId"), employeeId));
            }
            if (assessmentType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("assessmentType"), assessmentType));
            }
            if (fitnessDecision != null) {
                predicate = cb.and(predicate, cb.equal(root.get("fitnessDecision"), fitnessDecision));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (conductedById != null) {
                predicate = cb.and(predicate, cb.equal(root.get("conductedById"), conductedById));
            }
            if (assessmentFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("assessmentDate"), assessmentFrom));
            }
            if (assessmentTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("assessmentDate"), assessmentTo));
            }
            if (nextFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("nextAssessmentDate"), nextFrom));
            }
            if (nextTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("nextAssessmentDate"), nextTo));
            }
            return predicate;
        };
    }
}