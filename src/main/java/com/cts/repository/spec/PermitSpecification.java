package com.cts.repository.spec;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.WorkPermit;
import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;

/**
 * Builds dynamic WHERE conditions for work permit queries (Story 19).
 */
public final class PermitSpecification {

    private PermitSpecification() { }

    public static Specification<WorkPermit> build(
            Long siteId, PermitType permitType, PermitStatus status,
            String workLocation, Long issuedToId, Long approvedById,
            LocalDateTime fromDateTime, LocalDateTime toDateTime) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (siteId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("siteId"), siteId));
            }
            if (permitType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("permitType"), permitType));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (workLocation != null && !workLocation.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("workLocation")), "%" + workLocation.toLowerCase() + "%"));
            }
            if (issuedToId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("issuedToId"), issuedToId));
            }
            if (approvedById != null) {
                predicate = cb.and(predicate, cb.equal(root.get("approvedById"), approvedById));
            }
            if (fromDateTime != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDateTime"), fromDateTime));
            }
            if (toDateTime != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("endDateTime"), toDateTime));
            }
            return predicate;
        };
    }
}