package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.Notification;
import com.cts.enums.NotificationStatus;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    // For the unread-count endpoint (Story 24)
    long countByUserIdAndStatus(Long userId, NotificationStatus status);

    // For bulk status update - fetch the requested notifications for a user
    List<Notification> findByNotificationIdInAndUserId(List<Long> notificationIds, Long userId);
}