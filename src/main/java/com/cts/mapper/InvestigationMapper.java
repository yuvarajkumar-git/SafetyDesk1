package com.cts.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.cts.dto.request.InvestigationRequest;
import com.cts.dto.response.InvestigationResponse;
import com.cts.entity.IncidentInvestigation;
import com.cts.entity.IncidentReport;
import com.cts.entity.User;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.IncidentReportRepository;
import com.cts.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvestigationMapper {

    private final IncidentReportRepository incidentRepository;
    private final UserRepository userRepository;

    public IncidentInvestigation toEntity(InvestigationRequest request) {
        IncidentReport incident = incidentRepository.findById(request.getIncidentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Incident not found with id: " + request.getIncidentId()));
        User investigator = userRepository.findById(request.getInvestigatorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Investigator (User) not found with id: " + request.getInvestigatorId()));

        return IncidentInvestigation.builder()
                .incident(incident)
                .investigator(investigator)
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
                .incidentId(inv.getIncident() != null ? inv.getIncident().getIncidentId() : null)
                .investigatorId(inv.getInvestigator() != null ? inv.getInvestigator().getUserId() : null)
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