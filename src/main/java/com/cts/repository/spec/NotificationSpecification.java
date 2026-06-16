package com.cts.repository.spec;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.cts.entity.Notification;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;

/**
 * Builds dynamic WHERE conditions for notification queries (Story 24).
 */
public final class NotificationSpecification {

    private NotificationSpecification() { }

    public static Specification<Notification> build(
            Long userId, NotificationCategory category, NotificationStatus status,
            LocalDateTime fromDate, LocalDateTime toDate) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (userId != null) {
                // user is now a relationship -> join to its userId
                predicate = cb.and(predicate, cb.equal(root.get("user").get("userId"), userId));
            }
            if (category != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdDate"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdDate"), toDate));
            }
            return predicate;
        };
    }
}