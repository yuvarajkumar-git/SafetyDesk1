package com.cts.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.cts.dto.request.InvestigationRequest;
import com.cts.dto.response.InvestigationResponse;
import com.cts.entity.IncidentInvestigation;

@Component
public class InvestigationMapper {

    public IncidentInvestigation toEntity(InvestigationRequest request) {
        return IncidentInvestigation.builder()
                .incidentId(request.getIncidentId())
                .investigatorId(request.getInvestigatorId())
                .rootCauses(request.getRootCauses() != null ? request.getRootCauses() : new ArrayList<>())
                .contributingFactors(request.getContributingFactors() != null
                        ? request.getContributingFactors() : new ArrayList<>())
                .immediateActions(request.getImmediateActions())
                .lessonsLearned(request.getLessonsLearned())
                .investigationDate(request.getInvestigationDate())
                .build();
    }

    public InvestigationResponse toResponse(IncidentInvestigation inv) {
        return InvestigationResponse.builder()
                .investigationId(inv.getInvestigationId())
                .incidentId(inv.getIncidentId())
                .investigatorId(inv.getInvestigatorId())
                .rootCauses(inv.getRootCauses())
                .contributingFactors(inv.getContributingFactors())
                .immediateActions(inv.getImmediateActions())
                .lessonsLearned(inv.getLessonsLearned())
                .investigationDate(inv.getInvestigationDate())
                .status(inv.getStatus())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}