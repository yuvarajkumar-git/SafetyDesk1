package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.HazardRecord;
import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;

/**
 * Builds dynamic WHERE conditions for hazard queries (Story 15).
 */
public final class HazardSpecification {

    private HazardSpecification() { }

    public static Specification<HazardRecord> build(
            Long siteId, HazardType hazardType, HazardStatus status,
            String location, Long identifiedById,
            LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (siteId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("siteId"), siteId));
            }
            if (hazardType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("hazardType"), hazardType));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (location != null && !location.isBlank()) {
                // case-insensitive "contains" match on location
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (identifiedById != null) {
                predicate = cb.and(predicate, cb.equal(root.get("identifiedById"), identifiedById));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("identifiedDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("identifiedDate"), toDate));
            }
            return predicate;
        };
    }
}