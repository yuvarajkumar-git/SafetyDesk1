package com.cts.service;

import java.time.LocalDateTime;
import java.util.List;

import com.cts.dto.request.BulkStatusUpdateRequest;
import com.cts.dto.response.NotificationResponse;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;

public interface NotificationService {

    // Reusable trigger method called by other modules to raise a notification.
    NotificationResponse create(Long userId, String message, NotificationCategory category);

    List<NotificationResponse> search(Long userId, NotificationCategory category,
                                      NotificationStatus status,
                                      LocalDateTime fromDate, LocalDateTime toDate);

    NotificationResponse updateStatus(Long notificationId, NotificationStatus status);

    int bulkUpdateStatus(BulkStatusUpdateRequest request);

    long unreadCount(Long userId);
}