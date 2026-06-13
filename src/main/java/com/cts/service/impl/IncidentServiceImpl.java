package com.cts.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.IncidentRequest;
import com.cts.dto.request.InvestigatorAssignmentRequest;
import com.cts.dto.response.IncidentResponse;
import com.cts.entity.IncidentReport;
import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.Severity;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.IncidentMapper;
import com.cts.repository.IncidentReportRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.IncidentSpecification;
import com.cts.service.AuditLogService;
import com.cts.service.IncidentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentReportRepository incidentRepository;
    private final UserRepository userRepository;
    private final IncidentMapper incidentMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "IncidentReport";

    @Override
    @Transactional
    public IncidentResponse createIncident(IncidentRequest request) {
        log.info("Creating incident reported by user: {}", request.getReportedById());

        // Validate the reporter exists
        if (!userRepository.existsById(request.getReportedById())) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + request.getReportedById());
        }

        IncidentReport incident = incidentMapper.toEntity(request);
        // New incidents always start at the beginning of the lifecycle
        incident.setStatus(IncidentStatus.REPORTED);

        IncidentReport saved = incidentRepository.save(incident);
        auditLogService.record(saved.getReportedById(), "CREATE_INCIDENT", ENTITY_TYPE, saved.getIncidentId());

        log.info("Incident created with id: {}", saved.getIncidentId());
        return incidentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentResponse getIncidentById(Long incidentId) {
        return incidentMapper.toResponse(findIncidentOrThrow(incidentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentResponse> searchIncidents(Long siteId, IncidentType type, Severity severity,
                                                  IncidentStatus status, Long reportedById,
                                                  Long assignedInvestigatorId,
                                                  LocalDate fromDate, LocalDate toDate) {
        log.info("Searching incidents with filters");
        var spec = IncidentSpecification.build(
                siteId, type, severity, status, reportedById, assignedInvestigatorId, fromDate, toDate);
        return incidentRepository.findAll(spec).stream()
                .map(incidentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IncidentResponse assignInvestigator(Long incidentId, InvestigatorAssignmentRequest request) {
        log.info("Assigning investigator {} to incident {}", request.getAssignedInvestigatorId(), incidentId);
        // RBAC: only Safety Officer / EHS Manager may assign (enforced in security step)

        IncidentReport incident = findIncidentOrThrow(incidentId);

        // Investigator must be a real user
        if (!userRepository.existsById(request.getAssignedInvestigatorId())) {
            throw new ResourceNotFoundException(
                    "Investigator (User) not found with id: " + request.getAssignedInvestigatorId());
        }

        // Lifecycle guard: can only assign from REPORTED
        if (incident.getStatus() != IncidentStatus.REPORTED) {
            throw new IllegalArgumentException(
                    "Investigator can only be assigned when status is Reported. Current status: "
                            + incident.getStatus().getLabel());
        }

        incident.setAssignedInvestigatorId(request.getAssignedInvestigatorId());
        incident.setStatus(IncidentStatus.UNDER_INVESTIGATION);
        IncidentReport updated = incidentRepository.save(incident);

        auditLogService.record(updated.getAssignedInvestigatorId(),
                "ASSIGN_INVESTIGATOR", ENTITY_TYPE, updated.getIncidentId());

        log.info("Investigator assigned; incident {} now UnderInvestigation", incidentId);
        return incidentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public IncidentResponse updateStatus(Long incidentId, IncidentStatus newStatus) {
        log.info("Updating incident {} status to {}", incidentId, newStatus);
        // RBAC: only Safety Officer / EHS Manager may change status (enforced in security step)

        IncidentReport incident = findIncidentOrThrow(incidentId);
        validateTransition(incident.getStatus(), newStatus);

        incident.setStatus(newStatus);
        IncidentReport updated = incidentRepository.save(incident);

        auditLogService.record(updated.getReportedById(),
                "UPDATE_INCIDENT_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getIncidentId());

        return incidentMapper.toResponse(updated);
    }

    // Enforces the Story 12 lifecycle: Reported -> UnderInvestigation -> CAPAAssigned -> Closed
    private void validateTransition(IncidentStatus current, IncidentStatus next) {
        boolean ok = switch (current) {
            case REPORTED -> next == IncidentStatus.UNDER_INVESTIGATION;
            case UNDER_INVESTIGATION -> next == IncidentStatus.CAPA_ASSIGNED || next == IncidentStatus.CLOSED;
            case CAPA_ASSIGNED -> next == IncidentStatus.CLOSED;
            case CLOSED -> false; // terminal
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private IncidentReport findIncidentOrThrow(Long incidentId) {
        return incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Incident not found with id: " + incidentId));
    }
}