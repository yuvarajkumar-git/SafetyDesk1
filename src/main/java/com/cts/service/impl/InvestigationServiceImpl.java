package com.cts.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.InvestigationRequest;
import com.cts.dto.request.InvestigationUpdateRequest;
import com.cts.dto.response.InvestigationResponse;
import com.cts.entity.IncidentInvestigation;
import com.cts.entity.IncidentReport;
import com.cts.enums.IncidentStatus;
import com.cts.enums.InvestigationStatus;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.InvestigationMapper;
import com.cts.repository.IncidentInvestigationRepository;
import com.cts.repository.IncidentReportRepository;
import com.cts.service.AuditLogService;
import com.cts.service.InvestigationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {

    private final IncidentInvestigationRepository investigationRepository;
    private final IncidentReportRepository incidentRepository;
    private final InvestigationMapper investigationMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "IncidentInvestigation";

    @Override
    @Transactional
    public InvestigationResponse createInvestigation(InvestigationRequest request) {
        log.info("Creating investigation for incident: {}", request.getIncidentId());

        IncidentReport incident = incidentRepository.findById(request.getIncidentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Incident not found with id: " + request.getIncidentId()));

        // Story 13: incident must be UnderInvestigation
        if (incident.getStatus() != IncidentStatus.UNDER_INVESTIGATION) {
            throw new IllegalArgumentException(
                    "Investigation can only be created for an incident with status UnderInvestigation. Current: "
                            + incident.getStatus().getLabel());
        }

        // Story 13: investigator must match the incident's assigned investigator
        if (!request.getInvestigatorId().equals(incident.getAssignedInvestigatorId())) {
            throw new IllegalArgumentException(
                    "InvestigatorID must match the incident's assigned investigator ("
                            + incident.getAssignedInvestigatorId() + ")");
        }

        IncidentInvestigation investigation = investigationMapper.toEntity(request);
        investigation.setStatus(InvestigationStatus.IN_PROGRESS); // lifecycle start

        IncidentInvestigation saved = investigationRepository.save(investigation);
        auditLogService.record(saved.getInvestigatorId(), "CREATE_INVESTIGATION", ENTITY_TYPE, saved.getInvestigationId());

        log.info("Investigation created with id: {}", saved.getInvestigationId());
        return investigationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvestigationResponse getInvestigationById(Long investigationId) {
        return investigationMapper.toResponse(findOrThrow(investigationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestigationResponse> getByIncidentId(Long incidentId) {
        return investigationRepository.findByIncidentId(incidentId).stream()
                .map(investigationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestigationResponse> getByStatus(InvestigationStatus status) {
        return investigationRepository.findByStatus(status).stream()
                .map(investigationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestigationResponse> getAll() {
        return investigationRepository.findAll().stream()
                .map(investigationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvestigationResponse updateFindings(Long investigationId, InvestigationUpdateRequest request) {
        log.info("Updating findings for investigation: {}", investigationId);
        IncidentInvestigation inv = findOrThrow(investigationId);

        if (request.getRootCauses() != null)          inv.setRootCauses(request.getRootCauses());
        if (request.getContributingFactors() != null) inv.setContributingFactors(request.getContributingFactors());
        if (request.getImmediateActions() != null)    inv.setImmediateActions(request.getImmediateActions());
        if (request.getLessonsLearned() != null)       inv.setLessonsLearned(request.getLessonsLearned());

        IncidentInvestigation updated = investigationRepository.save(inv);
        auditLogService.record(updated.getInvestigatorId(), "UPDATE_INVESTIGATION", ENTITY_TYPE, updated.getInvestigationId());
        return investigationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public InvestigationResponse updateStatus(Long investigationId, InvestigationStatus newStatus,
                                              boolean correctiveActionsNeeded) {
        log.info("Updating investigation {} status to {}", investigationId, newStatus);
        // RBAC: only EHS Manager may approve/reject (enforced in security step)

        IncidentInvestigation inv = findOrThrow(investigationId);
        validateTransition(inv.getStatus(), newStatus);

        // Story 13: LessonsLearned mandatory before Completed
        if (newStatus == InvestigationStatus.COMPLETED
                && (inv.getLessonsLearned() == null || inv.getLessonsLearned().isBlank())) {
            throw new IllegalArgumentException(
                    "LessonsLearned must be filled in before an investigation can be Completed");
        }

        inv.setStatus(newStatus);
        IncidentInvestigation updated = investigationRepository.save(inv);
        auditLogService.record(updated.getInvestigatorId(),
                "UPDATE_INVESTIGATION_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getInvestigationId());

        // Story 13: completing an investigation pushes the incident to CAPAAssigned if CAPA is needed
        if (newStatus == InvestigationStatus.COMPLETED && correctiveActionsNeeded) {
            IncidentReport incident = incidentRepository.findById(inv.getIncidentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Incident not found with id: " + inv.getIncidentId()));
            if (incident.getStatus() == IncidentStatus.UNDER_INVESTIGATION) {
                incident.setStatus(IncidentStatus.CAPA_ASSIGNED);
                incidentRepository.save(incident);
                auditLogService.record(incident.getReportedById(),
                        "UPDATE_INCIDENT_STATUS_CAPA_ASSIGNED", "IncidentReport", incident.getIncidentId());
                log.info("Incident {} moved to CAPAAssigned", incident.getIncidentId());
            }
        }

        return investigationMapper.toResponse(updated);
    }

    // Story 13 lifecycle: InProgress -> Completed -> PendingApproval,
    // plus EHS Manager approve/reject (PendingApproval -> Completed or back to InProgress)
    private void validateTransition(InvestigationStatus current, InvestigationStatus next) {
        boolean ok = switch (current) {
            case IN_PROGRESS -> next == InvestigationStatus.COMPLETED;
            case COMPLETED -> next == InvestigationStatus.PENDING_APPROVAL;
            case PENDING_APPROVAL -> next == InvestigationStatus.COMPLETED || next == InvestigationStatus.IN_PROGRESS;
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid investigation status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private IncidentInvestigation findOrThrow(Long id) {
        return investigationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investigation not found with id: " + id));
    }
}