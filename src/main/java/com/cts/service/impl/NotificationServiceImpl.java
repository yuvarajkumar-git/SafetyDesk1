package com.cts.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.BulkStatusUpdateRequest;
import com.cts.dto.response.NotificationResponse;
import com.cts.entity.Notification;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.NotificationMapper;
import com.cts.repository.NotificationRepository;
import com.cts.repository.spec.NotificationSpecification;
import com.cts.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse create(Long userId, String message, NotificationCategory category) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .category(category)
                .status(NotificationStatus.UNREAD) // always starts Unread
                .createdDate(LocalDateTime.now())
                .build();
        Notification saved = notificationRepository.save(notification);
        // Story 24: every notification creation is logged (no AuditLog needed for reads/dismissals)
        log.info("Notification created: id={}, userId={}, category={}", saved.getNotificationId(), userId, category);
        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> search(Long userId, NotificationCategory category,
                                             NotificationStatus status,
                                             LocalDateTime fromDate, LocalDateTime toDate) {
        var spec = NotificationSpecification.build(userId, category, status, fromDate, toDate);
        return notificationRepository.findAll(spec).stream()
                .map(notificationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse updateStatus(Long notificationId, NotificationStatus status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + notificationId));
        notification.setStatus(status);
        Notification updated = notificationRepository.save(notification);
        log.info("Notification {} status updated to {}", notificationId, status);
        return notificationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public int bulkUpdateStatus(BulkStatusUpdateRequest request) {
        // Story 24: mark multiple notifications Read/Dismissed (scoped to the user)
        List<Notification> notifications = notificationRepository.findByNotificationIdInAndUserId(
                request.getNotificationIds(), request.getUserId());
        for (Notification n : notifications) {
            n.setStatus(request.getStatus());
            notificationRepository.save(n);
        }
        log.info("Bulk-updated {} notifications to {} for user {}",
                notifications.size(), request.getStatus(), request.getUserId());
        return notifications.size();
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }
}