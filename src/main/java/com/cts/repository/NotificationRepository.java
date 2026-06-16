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

    // renamed: user is now a relationship -> user.userId
    long countByUser_UserIdAndStatus(Long userId, NotificationStatus status);

    // renamed: user.userId in the join; notificationId is plain on Notification
    List<Notification> findByNotificationIdInAndUser_UserId(List<Long> notificationIds, Long userId);
}