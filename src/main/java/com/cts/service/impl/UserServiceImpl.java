package com.cts.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.UserRequest;
import com.cts.dto.request.UserUpdateRequest;
import com.cts.dto.response.UserResponse;
import com.cts.entity.User;
import com.cts.enums.UserStatus;
import com.cts.exception.DuplicateResourceException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.UserMapper;
import com.cts.repository.UserRepository;
import com.cts.service.AuditLogService;
import com.cts.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "User";

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        // Story 9: enforce unique email -> 409 if duplicate
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new DuplicateResourceException(
                    "A user with email '" + request.getEmail() + "' already exists");
        }

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);

        // Story 9: registration must generate an audit log entry
        auditLogService.record(saved.getUserId(), "CREATE_USER", ENTITY_TYPE, saved.getUserId());

        log.info("User created successfully with id: {}", saved.getUserId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user with id: {}", userId);
        User user = findUserOrThrow(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        log.info("Updating user with id: {}", userId);
        User user = findUserOrThrow(userId);

        // If email is changing, make sure the new one isn't taken by someone else
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException(
                        "A user with email '" + request.getEmail() + "' already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Only update fields that were actually provided
        if (request.getName() != null)         user.setName(request.getName());
        if (request.getRole() != null)         user.setRole(request.getRole());
        if (request.getPhone() != null)        user.setPhone(request.getPhone());
        if (request.getSiteId() != null)       user.setSiteId(request.getSiteId());
        if (request.getDepartmentId() != null) user.setDepartmentId(request.getDepartmentId());
        if (request.getStatus() != null)       user.setStatus(request.getStatus());

        User updated = userRepository.save(user);
        auditLogService.record(updated.getUserId(), "UPDATE_USER", ENTITY_TYPE, updated.getUserId());

        log.info("User updated successfully with id: {}", updated.getUserId());
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        log.info("Deactivating (soft-delete) user with id: {}", userId);
        User user = findUserOrThrow(userId);

        // Story 9: soft-delete = set Status to Inactive, NOT a hard delete
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        auditLogService.record(userId, "DEACTIVATE_USER", ENTITY_TYPE, userId);
        log.info("User deactivated successfully with id: {}", userId);
    }

    // Shared private helper to avoid repeating the not-found logic
    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
    }
}