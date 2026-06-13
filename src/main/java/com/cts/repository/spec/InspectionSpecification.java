package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.InspectionSchedule;
import com.cts.enums.InspectionStatus;
import com.cts.enums.InspectionType;

/**
 * Builds dynamic WHERE conditions for inspection schedule queries (Story 17).
 */
public final class InspectionSpecification {

    private InspectionSpecification() { }

    public static Specification<InspectionSchedule> build(
            Long siteId, InspectionType inspectionType, Long assignedOfficerId,
            InspectionStatus status, LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (siteId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("siteId"), siteId));
            }
            if (inspectionType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("inspectionType"), inspectionType));
            }
            if (assignedOfficerId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("assignedOfficerId"), assignedOfficerId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("plannedDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("plannedDate"), toDate));
            }
            return predicate;
        };
    }
}