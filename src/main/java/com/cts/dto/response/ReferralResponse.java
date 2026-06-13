package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.ReferralStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferralResponse {

    private Long referralId;
    private Long healthRecordId;
    private Long employeeId;
    private String referralReason;
    private String referredToSpeciality;
    private LocalDate referralDate;
    private String outcomeSummary;
    private ReferralStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}