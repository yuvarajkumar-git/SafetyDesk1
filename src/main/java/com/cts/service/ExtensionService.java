package com.cts.service;

import java.util.List;

import com.cts.dto.request.ExtensionDecisionRequest;
import com.cts.dto.request.ExtensionRequest;
import com.cts.dto.response.ExtensionResponse;
import com.cts.enums.ExtensionStatus;

public interface ExtensionService {

    ExtensionResponse requestExtension(ExtensionRequest request);

    ExtensionResponse getExtensionById(Long extensionId);

    List<ExtensionResponse> getByPermitId(Long permitId);

    List<ExtensionResponse> getByStatus(ExtensionStatus status);

    ExtensionResponse approveExtension(Long extensionId, ExtensionDecisionRequest request);

    ExtensionResponse rejectExtension(Long extensionId, ExtensionDecisionRequest request);
}