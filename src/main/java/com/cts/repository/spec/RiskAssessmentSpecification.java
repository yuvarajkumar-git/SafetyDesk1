package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.RiskAssessment;
import com.cts.enums.RiskAssessmentStatus;

/**
 * Builds dynamic WHERE conditions for risk assessment queries (Story 16).
 */
public final class RiskAssessmentSpecification {

    private RiskAssessmentSpecification() { }

    public static Specification<RiskAssessment> build(
            Long hazardId, Integer minRating, Integer maxRating,
            RiskAssessmentStatus status, Long assessedById,
            LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (hazardId != null) {
                // hazard is now a relationship -> join to its hazardId
                predicate = cb.and(predicate, cb.equal(root.get("hazard").get("hazardId"), hazardId));
            }
            if (minRating != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("riskRating"), minRating));
            }
            if (maxRating != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("riskRating"), maxRating));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (assessedById != null) {
                predicate = cb.and(predicate, cb.equal(root.get("assessedBy").get("userId"), assessedById));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("assessmentDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("assessmentDate"), toDate));
            }
            return predicate;
        };
    }
}