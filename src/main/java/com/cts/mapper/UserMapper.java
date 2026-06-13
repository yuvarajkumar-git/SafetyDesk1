package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.UserRequest;
import com.cts.dto.response.UserResponse;
import com.cts.entity.User;
import com.cts.enums.UserStatus;

/**
 * Converts between User entity and its DTOs.
 * Keeps conversion logic out of the service and controller.
 */
@Component
public class UserMapper {

    // Request DTO -> new Entity (used on create)
    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.getName())
                .role(request.getRole())
                .email(request.getEmail())
                .phone(request.getPhone())
                .siteId(request.getSiteId())
                .departmentId(request.getDepartmentId())
                // default to ACTIVE if the client didn't send a status
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .password(request.getPassword())
                .build();
    }

    // Entity -> Response DTO (used when returning data; note: no password)
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .siteId(user.getSiteId())
                .departmentId(user.getDepartmentId())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}