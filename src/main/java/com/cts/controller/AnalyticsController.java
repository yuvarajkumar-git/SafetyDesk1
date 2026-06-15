package com.cts.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.response.ApiResponse;
import com.cts.dto.response.EHSReportResponse;
import com.cts.dto.response.MetricsResponse;
import com.cts.enums.ReportScope;
import com.cts.enums.TrendGranularity;
import com.cts.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Analytics / EHS reporting (Story 23).
 * Base path: /api/analytics. Restricted to EHSManager/ComplianceOfficer/Admin (see SecurityConfig).
 */
@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<EHSReportResponse>> generate(
            @RequestParam(defaultValue = "PERIOD") ReportScope scope,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        EHSReportResponse report = analyticsService.generateReport(scope, siteId, departmentId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Report generated successfully", report));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<EHSReportResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Report retrieved", analyticsService.getReportById(id)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<EHSReportResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved", analyticsService.getAllReports()));
    }

    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<List<MetricsResponse>>> trend(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "MONTHLY") TrendGranularity granularity) {
        return ResponseEntity.ok(ApiResponse.success("Trend computed",
                analyticsService.trend(siteId, fromDate, toDate, granularity)));
    }

    @GetMapping("/reports/{id}/export/csv")
    public ResponseEntity<String> exportCsv(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ehs-report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(analyticsService.exportCsv(id));
    }

    @GetMapping("/reports/{id}/export/pdf")
    public ResponseEntity<String> exportPdf(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(analyticsService.exportHtml(id));
    }
}