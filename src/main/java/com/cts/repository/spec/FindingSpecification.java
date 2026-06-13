package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.InspectionFinding;
import com.cts.enums.FindingStatus;
import com.cts.enums.FindingType;
import com.cts.enums.RiskLevel;

/**
 * Builds dynamic WHERE conditions for inspection finding queries (Story 18).
 */
public final class FindingSpecification {

    private FindingSpecification() { }

    public static Specification<InspectionFinding> build(
            Long scheduleId, FindingType findingType, RiskLevel riskLevel,
            FindingStatus status, Long assignedToId,
            LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (scheduleId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("scheduleId"), scheduleId));
            }
            if (findingType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("findingType"), findingType));
            }
            if (riskLevel != null) {
                predicate = cb.and(predicate, cb.equal(root.get("riskLevel"), riskLevel));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (assignedToId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("assignedToId"), assignedToId));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dueDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dueDate"), toDate));
            }
            return predicate;
        };
    }
}