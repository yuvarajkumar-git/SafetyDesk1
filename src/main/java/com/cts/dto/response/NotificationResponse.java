package com.cts.dto.response;

import java.time.LocalDateTime;

import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {

    private Long notificationId;
    private Long userId;
    private String message;
    private NotificationCategory category;
    private NotificationStatus status;
    private LocalDateTime createdDate;
}