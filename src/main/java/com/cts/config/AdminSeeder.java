package com.cts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cts.entity.User;
import com.cts.enums.Role;
import com.cts.enums.UserStatus;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds one Admin user on startup if no user with the seed email exists,
 * so the system is never locked out after security is enabled.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${safetydesk.seed.admin-email}")
    private String adminEmail;

    @Value("${safetydesk.seed.admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Seed admin already present ({})", adminEmail);
            return;
        }
        User admin = User.builder()
                .name("System Admin")
                .role(Role.ADMIN)
                .email(adminEmail)
                .siteId(1L)
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(adminPassword))
                .build();
        userRepository.save(admin);
        log.info(">>> Seed admin created: {} (change the password after first login)", adminEmail);
    }
}