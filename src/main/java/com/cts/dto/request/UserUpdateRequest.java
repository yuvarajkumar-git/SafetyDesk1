package com.cts.dto.request;

import com.cts.enums.Role;
import com.cts.enums.UserStatus;

import jakarta.validation.constraints.Email;

import lombok.Data;

/**
 * Incoming payload for updating a User (Story 9: update profile/role/status).
 * All fields optional — only the provided ones are changed.
 */
@Data
public class UserUpdateRequest {

    private String name;
    private Role role;

    @Email(message = "Email must be a valid email address")
    private String email;

    private String phone;
    private Long siteId;
    private Long departmentId;
    private UserStatus status;
}