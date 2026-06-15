package com.cts.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.ReportScope;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EHSReportResponse {

    private Long reportId;
    private ReportScope scope;
    private Long siteId;
    private Long departmentId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private MetricsResponse metrics;
    private LocalDateTime generatedDate;
}