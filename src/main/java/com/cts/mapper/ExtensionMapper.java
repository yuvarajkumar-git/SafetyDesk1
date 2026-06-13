package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.request.ExtensionRequest;
import com.cts.dto.response.ExtensionResponse;
import com.cts.entity.PermitExtension;

@Component
public class ExtensionMapper {

    public PermitExtension toEntity(ExtensionRequest request) {
        return PermitExtension.builder()
                .permitId(request.getPermitId())
                .requestedById(request.getRequestedById())
                .newEndDateTime(request.getNewEndDateTime())
                .reason(request.getReason())
                // status set by the service
                .build();
    }

    public ExtensionResponse toResponse(PermitExtension ext) {
        return ExtensionResponse.builder()
                .extensionId(ext.getExtensionId())
                .permitId(ext.getPermitId())
                .requestedById(ext.getRequestedById())
                .newEndDateTime(ext.getNewEndDateTime())
                .reason(ext.getReason())
                .approvedById(ext.getApprovedById())
                .status(ext.getStatus())
                .createdAt(ext.getCreatedAt())
                .updatedAt(ext.getUpdatedAt())
                .build();
    }
}