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
import com.cts.enums.NotificationCategory;
import com.cts.enums.Role;
import com.cts.enums.Severity;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.IncidentMapper;
import com.cts.repository.IncidentReportRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.IncidentSpecification;
import com.cts.security.CurrentUser;
import com.cts.service.AuditLogService;
import com.cts.service.IncidentService;
import com.cts.service.NotificationRouter;

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
    private final CurrentUser currentUser;
    private final NotificationRouter notificationRouter;

    private static final String ENTITY_TYPE = "IncidentReport";

    @Override
    @Transactional
    public IncidentResponse createIncident(IncidentRequest request) {
        // Story 12: ReportedByID comes from the authenticated user, not the request body
        Long reporterId = currentUser.id();
        log.info("Creating incident reported by authenticated user: {}", reporterId);

        IncidentReport incident = incidentMapper.toEntity(request);
        incident.setReportedById(reporterId);
        incident.setStatus(IncidentStatus.REPORTED);

        IncidentReport saved = incidentRepository.save(incident);
        auditLogService.record(reporterId, "CREATE_INCIDENT", ENTITY_TYPE, saved.getIncidentId());

        // Story 24: new incident -> notify Safety Officers at the incident's site
        notificationRouter.notifyRoleAtSite(Role.SAFETY_OFFICER, saved.getSiteId(),
                "New incident reported (#" + saved.getIncidentId() + ", " + saved.getIncidentType().getLabel()
                        + ") at site " + saved.getSiteId(),
                NotificationCategory.INCIDENT);

        // Story 24: Serious/Fatal -> escalate to EHS Manager
        if (saved.getSeverity() == Severity.SERIOUS || saved.getSeverity() == Severity.FATAL) {
            notificationRouter.notifyRole(Role.EHS_MANAGER,
                    "ESCALATION: " + saved.getSeverity().getLabel() + " incident #" + saved.getIncidentId()
                            + " reported at site " + saved.getSiteId(),
                    NotificationCategory.INCIDENT);
        }

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

        IncidentReport incident = findIncidentOrThrow(incidentId);

        if (!userRepository.existsById(request.getAssignedInvestigatorId())) {
            throw new ResourceNotFoundException(
                    "Investigator (User) not found with id: " + request.getAssignedInvestigatorId());
        }

        if (incident.getStatus() != IncidentStatus.REPORTED) {
            throw new IllegalArgumentException(
                    "Investigator can only be assigned when status is Reported. Current status: "
                            + incident.getStatus().getLabel());
        }

        incident.setAssignedInvestigatorId(request.getAssignedInvestigatorId());
        incident.setStatus(IncidentStatus.UNDER_INVESTIGATION);
        IncidentReport updated = incidentRepository.save(incident);

        auditLogService.record(currentUser.id(),
                "ASSIGN_INVESTIGATOR", ENTITY_TYPE, updated.getIncidentId());

        // Story 24: investigation assigned -> notify the assigned investigator
        notificationRouter.notifyUser(updated.getAssignedInvestigatorId(),
                "You have been assigned as investigator for incident #" + updated.getIncidentId(),
                NotificationCategory.INCIDENT);

        log.info("Investigator assigned; incident {} now UnderInvestigation", incidentId);
        return incidentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public IncidentResponse updateStatus(Long incidentId, IncidentStatus newStatus) {
        log.info("Updating incident {} status to {}", incidentId, newStatus);

        IncidentReport incident = findIncidentOrThrow(incidentId);
        validateTransition(incident.getStatus(), newStatus);

        incident.setStatus(newStatus);
        IncidentReport updated = incidentRepository.save(incident);

        auditLogService.record(currentUser.id(),
                "UPDATE_INCIDENT_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getIncidentId());

        return incidentMapper.toResponse(updated);
    }

    private void validateTransition(IncidentStatus current, IncidentStatus next) {
        boolean ok = switch (current) {
            case REPORTED -> next == IncidentStatus.UNDER_INVESTIGATION;
            case UNDER_INVESTIGATION -> next == IncidentStatus.CAPA_ASSIGNED || next == IncidentStatus.CLOSED;
            case CAPA_ASSIGNED -> next == IncidentStatus.CLOSED;
            case CLOSED -> false;
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