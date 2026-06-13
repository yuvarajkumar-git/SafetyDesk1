package com.cts.service;

import java.util.List;

import com.cts.dto.request.UserRequest;
import com.cts.dto.request.UserUpdateRequest;
import com.cts.dto.response.UserResponse;

/**
 * Business operations for User (Story 9): full CRUD plus soft-delete.
 */
public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    // Soft-delete = deactivation (Story 9), not a hard DB delete
    void deactivateUser(Long userId);
}