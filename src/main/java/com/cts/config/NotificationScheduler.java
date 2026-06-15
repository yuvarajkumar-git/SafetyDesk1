package com.cts.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cts.service.HealthRecordService;
import com.cts.service.InspectionService;
import com.cts.service.PermitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Periodically fires Story 24 time-based reminders and auto-status sweeps.
 * Disable by removing @EnableScheduling on the application class, or adjust
 * the fixedRate values. Times are in milliseconds.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final PermitService permitService;
    private final InspectionService inspectionService;
    private final HealthRecordService healthRecordService;

    // every 30 minutes: expire/miss sweeps + expiry/reminder notifications
    @Scheduled(fixedRate = 1_800_000)
    public void run() {
        log.debug("Running scheduled notification sweeps");
        permitService.markExpiredPermits();
        permitService.remindExpiringPermits(2);          // default 2h (Story 24)
        inspectionService.markMissedInspections();
        inspectionService.remindUpcomingInspections(1);  // ~24h ahead (Story 24 default)
        healthRecordService.remindUpcomingAssessments(7);// 7 days (Story 24 default)
    }
}
