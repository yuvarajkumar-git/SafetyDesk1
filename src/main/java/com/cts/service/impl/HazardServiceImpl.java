package com.cts.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.request.HazardRequest;
import com.cts.dto.request.HazardUpdateRequest;
import com.cts.dto.response.HazardResponse;
import com.cts.entity.HazardRecord;
import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;
import com.cts.exception.ResourceNotFoundException;
import com.cts.mapper.HazardMapper;
import com.cts.repository.HazardRecordRepository;
import com.cts.repository.UserRepository;
import com.cts.repository.spec.HazardSpecification;
import com.cts.service.AuditLogService;
import com.cts.service.HazardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HazardServiceImpl implements HazardService {

    private final HazardRecordRepository hazardRepository;
    private final UserRepository userRepository;
    private final HazardMapper hazardMapper;
    private final AuditLogService auditLogService;

    private static final String ENTITY_TYPE = "HazardRecord";

    @Override
    @Transactional
    public HazardResponse createHazard(HazardRequest request) {
        log.info("Recording hazard at site {} by user {}", request.getSiteId(), request.getIdentifiedById());
        // AUTH: identifiedById will come from the authenticated user later.

        // IdentifiedByID must reference a valid user
        if (!userRepository.existsById(request.getIdentifiedById())) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + request.getIdentifiedById());
        }

        HazardRecord hazard = hazardMapper.toEntity(request);
        hazard.setStatus(HazardStatus.OPEN); // lifecycle start

        HazardRecord saved = hazardRepository.save(hazard);
        auditLogService.record(saved.getIdentifiedById(), "CREATE_HAZARD", ENTITY_TYPE, saved.getHazardId());

        log.info("Hazard recorded with id: {}", saved.getHazardId());
        return hazardMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HazardResponse getHazardById(Long hazardId) {
        return hazardMapper.toResponse(findOrThrow(hazardId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HazardResponse> searchHazards(Long siteId, HazardType hazardType, HazardStatus status,
                                              String location, Long identifiedById,
                                              LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        log.info("Searching hazards (paged)");
        // RBAC: results will be restricted to the user's site(s) later.
        var spec = HazardSpecification.build(siteId, hazardType, status, location, identifiedById, fromDate, toDate);
        return hazardRepository.findAll(spec, pageable).map(hazardMapper::toResponse);
    }

    @Override
    @Transactional
    public HazardResponse updateHazard(Long hazardId, HazardUpdateRequest request) {
        log.info("Updating hazard: {}", hazardId);
        HazardRecord hazard = findOrThrow(hazardId);

        if (request.getLocation() != null)    hazard.setLocation(request.getLocation());
        if (request.getHazardType() != null)  hazard.setHazardType(request.getHazardType());
        if (request.getDescription() != null) hazard.setDescription(request.getDescription());

        HazardRecord updated = hazardRepository.save(hazard);
        auditLogService.record(updated.getIdentifiedById(), "UPDATE_HAZARD", ENTITY_TYPE, updated.getHazardId());
        return hazardMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public HazardResponse updateStatus(Long hazardId, HazardStatus newStatus) {
        log.info("Updating hazard {} status to {}", hazardId, newStatus);
        HazardRecord hazard = findOrThrow(hazardId);
        validateTransition(hazard.getStatus(), newStatus);

        hazard.setStatus(newStatus);
        HazardRecord updated = hazardRepository.save(hazard);
        auditLogService.record(updated.getIdentifiedById(),
                "UPDATE_HAZARD_STATUS_" + newStatus.name(), ENTITY_TYPE, updated.getHazardId());
        return hazardMapper.toResponse(updated);
    }

    // Story 15 lifecycle: Open -> Mitigated -> Closed, plus Recurring (reappears after mitigation)
    private void validateTransition(HazardStatus current, HazardStatus next) {
        boolean ok = switch (current) {
            case OPEN -> next == HazardStatus.MITIGATED;
            case MITIGATED -> next == HazardStatus.CLOSED || next == HazardStatus.RECURRING;
            case RECURRING -> next == HazardStatus.MITIGATED || next == HazardStatus.CLOSED;
            case CLOSED -> next == HazardStatus.RECURRING; // a closed hazard can reappear
        };
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid hazard status transition from " + current.getLabel() + " to " + next.getLabel());
        }
    }

    private HazardRecord findOrThrow(Long hazardId) {
        return hazardRepository.findById(hazardId)
                .orElseThrow(() -> new ResourceNotFoundException("Hazard not found with id: " + hazardId));
    }
}