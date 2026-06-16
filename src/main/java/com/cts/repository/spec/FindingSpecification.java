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
                // schedule is now a relationship -> join to its scheduleId
                predicate = cb.and(predicate, cb.equal(root.get("schedule").get("scheduleId"), scheduleId));
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
                // assignedTo is now a relationship -> join to its userId
                predicate = cb.and(predicate, cb.equal(root.get("assignedTo").get("userId"), assignedToId));
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