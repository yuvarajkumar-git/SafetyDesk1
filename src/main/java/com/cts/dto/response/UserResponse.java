package com.cts.dto.response;

import java.time.LocalDateTime;

import com.cts.enums.Role;
import com.cts.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

/**
 * Outgoing payload representing a User.
 * Notice: NO password field — it is never exposed.
 */
@Data
@Builder
public class UserResponse {

    private Long userId;
    private String name;
    private Role role;
    private String email;
    private String phone;
    private Long siteId;
    private Long departmentId;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}