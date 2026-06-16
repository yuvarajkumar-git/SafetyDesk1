package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.ExtensionRequest;
import com.cts.dto.response.ExtensionResponse;
import com.cts.entity.PermitExtension;
import com.cts.entity.User;
import com.cts.entity.WorkPermit;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.UserRepository;
import com.cts.repository.WorkPermitRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExtensionMapper {

    private final WorkPermitRepository permitRepository;
    private final UserRepository userRepository;

    public PermitExtension toEntity(ExtensionRequest request) {
        WorkPermit permit = permitRepository.findById(request.getPermitId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Permit not found with id: " + request.getPermitId()));
        User requestedBy = userRepository.findById(request.getRequestedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Requester (User) not found with id: " + request.getRequestedById()));

        return PermitExtension.builder()
                .permit(permit)
                .requestedBy(requestedBy)
                .newEndDateTime(request.getNewEndDateTime())
                .reason(request.getReason())
                // approvedBy and status are set by the service
                .build();
    }

    public ExtensionResponse toResponse(PermitExtension ext) {
        return ExtensionResponse.builder()
                .extensionId(ext.getExtensionId())
                .permitId(ext.getPermit() != null ? ext.getPermit().getPermitId() : null)
                .requestedById(ext.getRequestedBy() != null ? ext.getRequestedBy().getUserId() : null)
                .newEndDateTime(ext.getNewEndDateTime())
                .reason(ext.getReason())
                .approvedById(ext.getApprovedBy() != null ? ext.getApprovedBy().getUserId() : null)
                .status(ext.getStatus())
                .createdAt(ext.getCreatedAt())
                .updatedAt(ext.getUpdatedAt())
                .build();
    }
}