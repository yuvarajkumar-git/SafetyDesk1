package com.cts.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cts.entity.User;
import com.cts.enums.NotificationCategory;
import com.cts.enums.Role;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Resolves role-targeted notifications to concrete users and dispatches them
 * via NotificationService. Used to wire Story 24 triggers that target roles
 * (e.g. "notify Safety Officer", "escalate to EHS Manager").
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRouter {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /** Notify every user holding the given role (system-wide). */
    public void notifyRole(Role role, String message, NotificationCategory category) {
        List<User> recipients = userRepository.findByRole(role);
        dispatch(recipients, message, category, role);
    }

    /** Notify every user holding the given role at a specific site. */
    public void notifyRoleAtSite(Role role, Long siteId, String message, NotificationCategory category) {
        if (siteId == null) {
            notifyRole(role, message, category);
            return;
        }
        List<User> recipients = userRepository.findByRoleAndSiteId(role, siteId);
        // Fall back to system-wide if no one holds that role at the site,
        // so a notification is never silently lost.
        if (recipients.isEmpty()) {
            recipients = userRepository.findByRole(role);
        }
        dispatch(recipients, message, category, role);
    }

    /** Notify a single specific user (by id). */
    public void notifyUser(Long userId, String message, NotificationCategory category) {
        if (userId == null) {
            return;
        }
        notificationService.create(userId, message, category);
    }

    private void dispatch(List<User> recipients, String message,
                          NotificationCategory category, Role role) {
        if (recipients.isEmpty()) {
            log.warn("No users with role {} to notify: {}", role, message);
            return;
        }
        for (User u : recipients) {
            notificationService.create(u.getUserId(), message, category);
        }
        log.info("Notified {} user(s) with role {} [{}]", recipients.size(), role, category);
    }
}