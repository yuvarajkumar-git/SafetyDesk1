package com.cts.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.PermitExtension;
import com.cts.enums.ExtensionStatus;

@Repository
public interface PermitExtensionRepository extends JpaRepository<PermitExtension, Long> {

    // renamed: permit is now a relationship -> permit.permitId
    List<PermitExtension> findByPermit_PermitId(Long permitId);

    List<PermitExtension> findByStatus(ExtensionStatus status);

    // renamed: requestedBy is now a relationship -> requestedBy.userId
    List<PermitExtension> findByRequestedBy_UserId(Long requestedById);

    List<PermitExtension> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}