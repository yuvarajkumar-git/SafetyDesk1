package com.cts.dto.response;

import java.time.LocalDateTime;

import com.cts.enums.ExtensionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtensionResponse {

    private Long extensionId;
    private Long permitId;
    private Long requestedById;
    private LocalDateTime newEndDateTime;
    private String reason;
    private Long approvedById;
    private ExtensionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}