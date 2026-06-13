package com.cts.repository.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.MedicalReferral;
import com.cts.enums.ReferralStatus;

/**
 * Builds dynamic WHERE conditions for medical referral queries (Story 22).
 */
public final class ReferralSpecification {

    private ReferralSpecification() { }

    public static Specification<MedicalReferral> build(
            Long employeeId, Long healthRecordId, ReferralStatus status,
            String referredToSpeciality, LocalDate fromDate, LocalDate toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (employeeId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("employeeId"), employeeId));
            }
            if (healthRecordId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("healthRecordId"), healthRecordId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (referredToSpeciality != null && !referredToSpeciality.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("referredToSpeciality")),
                                "%" + referredToSpeciality.toLowerCase() + "%"));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("referralDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("referralDate"), toDate));
            }
            return predicate;
        };
    }
}