package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.IncidentReport;
import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.Severity;

/**
 * Builds dynamic WHERE conditions for incident queries (Story 12).
 * Each method returns a condition that is only applied if its value is non-null.
 */
public final class IncidentSpecification {

    private IncidentSpecification() { } // utility class - no instances

    public static Specification<IncidentReport> build(
            Long siteId, IncidentType type, Severity severity, IncidentStatus status,
            Long reportedById, Long assignedInvestigatorId,
            LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction(); // start with "always true"

            if (siteId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("siteId"), siteId));
            }
            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("incidentType"), type));
            }
            if (severity != null) {
                predicate = cb.and(predicate, cb.equal(root.get("severity"), severity));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (reportedById != null) {
                predicate = cb.and(predicate, cb.equal(root.get("reportedById"), reportedById));
            }
            if (assignedInvestigatorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("assignedInvestigatorId"), assignedInvestigatorId));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("incidentDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("incidentDate"), toDate));
            }
            return predicate;
        };
    }
}