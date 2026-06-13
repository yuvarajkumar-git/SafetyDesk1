package com.cts.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.request.BulkStatusUpdateRequest;
import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.NotificationResponse;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;
import com.cts.service.NotificationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for Notification (Story 24).
 * Base path: /api/notifications
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) NotificationCategory category,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        List<NotificationResponse> results = notificationService.search(userId, category, status, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", results));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> unreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Unread count retrieved successfully", notificationService.unreadCount(userId)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<NotificationResponse>> updateStatus(
            @PathVariable Long id, @RequestParam NotificationStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Notification status updated successfully", notificationService.updateStatus(id, status)));
    }

    @PutMapping("/bulk-status")
    public ResponseEntity<ApiResponse<Integer>> bulkUpdate(
            @Valid @RequestBody BulkStatusUpdateRequest request) {
        int count = notificationService.bulkUpdateStatus(request);
        return ResponseEntity.ok(ApiResponse.success("Notifications updated successfully", count));
    }
}