package com.cts.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;

/**
 * Payload to create a medical referral (Story 22).
 */
@Data
public class ReferralRequest {

    @NotNull(message = "HealthRecordID is required")
    private Long healthRecordId;

    @NotNull(message = "EmployeeID is required")
    private Long employeeId;

    @NotBlank(message = "ReferralReason is required")
    private String referralReason;

    private String referredToSpeciality;

    @NotNull(message = "ReferralDate is required")
    @PastOrPresent(message = "ReferralDate cannot be in the future")
    private LocalDate referralDate;
}