package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.HealthRecord;

@Repository
public interface HealthRecordRepository
        extends JpaRepository<HealthRecord, Long>, JpaSpecificationExecutor<HealthRecord> {
}