package com.cts.service;

import java.time.LocalDateTime;
import java.util.List;

import com.cts.dto.request.PermitApprovalRequest;
import com.cts.dto.request.PermitRequest;
import com.cts.dto.request.PermitUpdateRequest;
import com.cts.dto.response.PermitResponse;
import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;

public interface PermitService {

    PermitResponse createPermit(PermitRequest request);

    PermitResponse getPermitById(Long permitId);

    List<PermitResponse> searchPermits(Long siteId, PermitType permitType, PermitStatus status,
                                       String workLocation, Long issuedToId, Long approvedById,
                                       LocalDateTime fromDateTime, LocalDateTime toDateTime);

    PermitResponse updatePermit(Long permitId, PermitUpdateRequest request);

    PermitResponse submitForApproval(Long permitId);

    PermitResponse approvePermit(Long permitId, PermitApprovalRequest request);

    PermitResponse activatePermit(Long permitId);

    PermitResponse updateStatus(Long permitId, PermitStatus newStatus);

    int markExpiredPermits();
}