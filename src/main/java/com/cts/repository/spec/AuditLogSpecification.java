package com.cts.repository.spec;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.AuditLog;

/**
 * Builds dynamic WHERE conditions for audit log queries (Story 11).
 */
public final class AuditLogSpecification {

    private AuditLogSpecification() { }

    public static Specification<AuditLog> build(
            Long userId, String entityType, Long recordId, String action,
            LocalDateTime fromDate, LocalDateTime toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (userId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("userId"), userId));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("entityType"), entityType));
            }
            if (recordId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("recordId"), recordId));
            }
            if (action != null && !action.isBlank()) {
                // partial match so "Login" also catches "FailedLogin", "UPDATE_..." prefixes, etc.
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("action")), "%" + action.toLowerCase() + "%"));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), toDate));
            }
            return predicate;
        };
    }
}