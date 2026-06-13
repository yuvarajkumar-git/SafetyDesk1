package com.cts.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.PermitExtension;
import com.cts.enums.ExtensionStatus;

@Repository
public interface PermitExtensionRepository extends JpaRepository<PermitExtension, Long> {

    List<PermitExtension> findByPermitId(Long permitId);

    List<PermitExtension> findByStatus(ExtensionStatus status);

    List<PermitExtension> findByRequestedById(Long requestedById);

    List<PermitExtension> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}