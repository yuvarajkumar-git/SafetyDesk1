package com.cts.dto.request;

import com.cts.enums.Role;
import com.cts.enums.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * Incoming payload for creating a User (Story 9).
 * Validation enforces the story's required fields: Name, Role, Email, SiteID.
 */
@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    private String phone;

    @NotNull(message = "SiteID is required")
    private Long siteId;

    private Long departmentId;

    // Optional on create; defaults to ACTIVE in the service if not provided
    private UserStatus status;

    @NotBlank(message = "Password is required")
    private String password;
}