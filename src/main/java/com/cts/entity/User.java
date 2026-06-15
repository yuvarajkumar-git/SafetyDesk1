package com.cts.entity;

import com.cts.enums.Role;
import com.cts.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User (Story 9): system user with role-based and site-scoped access.
 * Fields per story: UserID, Name, Role, Email, Phone, SiteID, DepartmentID, Status.
 * A password field is added (needed for Story 10 Login) but is never
 * exposed in any response DTO.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "department_id")
    private Long departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // Needed for Story 10 (Login). Never returned in responses.
    @Column(name = "password", nullable = false)
    private String password;
    
 // Story 10: account lockout tracking
    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    @Builder.Default
    private boolean accountLocked = false;
}