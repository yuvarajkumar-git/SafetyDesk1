package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.IncidentReport;
import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.Severity;

/**
 * Builds dynamic WHERE conditions for incident queries (Story 12).
 */
public final class IncidentSpecification {

    private IncidentSpecification() { }

    public static Specification<IncidentReport> build(
            Long siteId, IncidentType type, Severity severity, IncidentStatus status,
            Long reportedById, Long assignedInvestigatorId,
            LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

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
                // reportedBy is now a relationship -> join to its userId
                predicate = cb.and(predicate, cb.equal(root.get("reportedBy").get("userId"), reportedById));
            }
            if (assignedInvestigatorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("assignedInvestigator").get("userId"), assignedInvestigatorId));
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