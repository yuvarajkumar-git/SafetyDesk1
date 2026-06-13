package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.request.ReferralOutcomeRequest;
import com.cts.dto.request.ReferralRequest;
import com.cts.dto.response.ReferralResponse;
import com.cts.enums.ReferralStatus;

public interface ReferralService {

    ReferralResponse createReferral(ReferralRequest request);

    ReferralResponse getReferralById(Long referralId);

    List<ReferralResponse> searchReferrals(Long employeeId, Long healthRecordId, ReferralStatus status,
                                           String referredToSpeciality, LocalDate fromDate, LocalDate toDate);

    ReferralResponse updateOutcome(Long referralId, ReferralOutcomeRequest request);
}