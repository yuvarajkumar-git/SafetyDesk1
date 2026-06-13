package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.cts.entity.MedicalReferral;

@Repository
public interface MedicalReferralRepository
        extends JpaRepository<MedicalReferral, Long>, JpaSpecificationExecutor<MedicalReferral> {
}