package com.cts.service;

import java.time.LocalDate;
import java.util.List;

import com.cts.dto.response.EHSReportResponse;
import com.cts.dto.response.MetricsResponse;
import com.cts.enums.ReportScope;
import com.cts.enums.TrendGranularity;

public interface AnalyticsService {

    EHSReportResponse generateReport(ReportScope scope, Long siteId, Long departmentId,
                                     LocalDate fromDate, LocalDate toDate);

    EHSReportResponse getReportById(Long reportId);

    List<EHSReportResponse> getAllReports();

    // Trend analysis: a metrics bundle per time bucket in the range
    List<MetricsResponse> trend(Long siteId, LocalDate fromDate, LocalDate toDate, TrendGranularity granularity);

    String exportCsv(Long reportId);

    String exportHtml(Long reportId);
}