package com.cts.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.response.EHSReportResponse;
import com.cts.dto.response.MetricsResponse;
import com.cts.entity.EHSReport;
import com.cts.enums.CorrectiveActionStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.InspectionStatus;
import com.cts.enums.PermitStatus;
import com.cts.enums.ReportScope;
import com.cts.enums.TrendGranularity;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CorrectiveActionRepository;
import com.cts.repository.EHSReportRepository;
import com.cts.repository.IncidentReportRepository;
import com.cts.repository.InspectionScheduleRepository;
import com.cts.repository.WorkPermitRepository;
import com.cts.security.CurrentUser;
import com.cts.service.AnalyticsService;
import com.cts.service.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EHSReportRepository reportRepository;
    private final IncidentReportRepository incidentRepository;
    private final InspectionScheduleRepository inspectionRepository;
    private final CorrectiveActionRepository correctiveActionRepository;
    private final WorkPermitRepository permitRepository;
    private final AuditLogService auditLogService;
    private final CurrentUser currentUser;

    private static final String ENTITY_TYPE = "EHSReport";
    private static final String BLOCKED_NOTE =
            "LTIFR/TRIFR require man-hours data (Story 42) and RegulatoryObligationsOverdue "
            + "requires the regulatory obligations module (Story 40); both are out of the current scope, "
            + "so these metrics are returned as null.";

    @Override
    @Transactional
    public EHSReportResponse generateReport(ReportScope scope, Long siteId, Long departmentId,
                                            LocalDate fromDate, LocalDate toDate) {
        LocalDate to = (toDate != null) ? toDate : LocalDate.now();
        LocalDate from = (fromDate != null) ? fromDate : to.minusDays(30);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("fromDate must not be after toDate");
        }

        MetricsResponse metrics = computeMetrics(siteId, from, to);

        EHSReport report = EHSReport.builder()
                .scope(scope)
                .siteId(siteId)
                .departmentId(departmentId)
                .fromDate(from)
                .toDate(to)
                .totalIncidents(metrics.getTotalIncidents())
                .nearMissCount(metrics.getNearMissCount())
                .nearMissFrequencyRate(metrics.getNearMissFrequencyRate())
                .inspectionCompletionRate(metrics.getInspectionCompletionRate())
                .correctiveActionClosureRate(metrics.getCorrectiveActionClosureRate())
                .permitComplianceRate(metrics.getPermitComplianceRate())
                .ltifr(metrics.getLtifr())
                .trifr(metrics.getTrifr())
                .regulatoryObligationsOverdue(metrics.getRegulatoryObligationsOverdue())
                .generatedDate(LocalDateTime.now())
                .build();
        EHSReport saved = reportRepository.save(report);

        // Story 23: every report generation generates an audit entry
        auditLogService.record(currentUser.id(), "GENERATE_EHS_REPORT", ENTITY_TYPE, saved.getReportId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EHSReportResponse getReportById(Long reportId) {
        EHSReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
        return toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EHSReportResponse> getAllReports() {
        List<EHSReportResponse> out = new ArrayList<>();
        for (EHSReport r : reportRepository.findAll()) {
            out.add(toResponse(r));
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricsResponse> trend(Long siteId, LocalDate fromDate, LocalDate toDate,
                                       TrendGranularity granularity) {
        LocalDate to = (toDate != null) ? toDate : LocalDate.now();
        LocalDate from = (fromDate != null) ? fromDate : to.minusMonths(3);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("fromDate must not be after toDate");
        }

        List<MetricsResponse> buckets = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            LocalDate bucketEnd = switch (granularity) {
                case DAILY -> cursor;
                case WEEKLY -> cursor.plusDays(6);
                case MONTHLY -> cursor.plusMonths(1).minusDays(1);
            };
            if (bucketEnd.isAfter(to)) {
                bucketEnd = to;
            }
            buckets.add(computeMetrics(siteId, cursor, bucketEnd));
            cursor = switch (granularity) {
                case DAILY -> cursor.plusDays(1);
                case WEEKLY -> cursor.plusDays(7);
                case MONTHLY -> cursor.plusMonths(1);
            };
        }
        return buckets;
    }

    @Override
    @Transactional(readOnly = true)
    public String exportCsv(Long reportId) {
        EHSReportResponse r = getReportById(reportId);
        MetricsResponse m = r.getMetrics();
        StringBuilder sb = new StringBuilder();
        sb.append("Metric,Value\n");
        sb.append("TotalIncidents,").append(m.getTotalIncidents()).append('\n');
        sb.append("NearMissCount,").append(m.getNearMissCount()).append('\n');
        sb.append("NearMissFrequencyRate,").append(m.getNearMissFrequencyRate()).append('\n');
        sb.append("InspectionCompletionRate,").append(m.getInspectionCompletionRate()).append('\n');
        sb.append("CorrectiveActionClosureRate,").append(m.getCorrectiveActionClosureRate()).append('\n');
        sb.append("PermitComplianceRate,").append(m.getPermitComplianceRate()).append('\n');
        sb.append("LTIFR,").append(m.getLtifr() == null ? "N/A (Story 42)" : m.getLtifr()).append('\n');
        sb.append("TRIFR,").append(m.getTrifr() == null ? "N/A (Story 42)" : m.getTrifr()).append('\n');
        sb.append("RegulatoryObligationsOverdue,")
          .append(m.getRegulatoryObligationsOverdue() == null ? "N/A (Story 40)" : m.getRegulatoryObligationsOverdue())
          .append('\n');
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportHtml(Long reportId) {
        EHSReportResponse r = getReportById(reportId);
        MetricsResponse m = r.getMetrics();
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>SafetyDesk EHS Report</title>")
          .append("<style>body{font-family:Arial,sans-serif;margin:24px}h1{font-size:18px}")
          .append("table{border-collapse:collapse}td,th{border:1px solid #999;padding:6px 12px}</style></head><body>");
        sb.append("<h1>SafetyDesk EHS Report #").append(r.getReportId()).append("</h1>");
        sb.append("<p>Scope: ").append(r.getScope()).append(" &middot; ")
          .append(r.getFromDate()).append(" to ").append(r.getToDate())
          .append(" &middot; generated ").append(r.getGeneratedDate()).append("</p>");
        sb.append("<table><tr><th>Metric</th><th>Value</th></tr>");
        row(sb, "Total Incidents", String.valueOf(m.getTotalIncidents()));
        row(sb, "Near Miss Count", String.valueOf(m.getNearMissCount()));
        row(sb, "Near Miss Frequency Rate", String.valueOf(m.getNearMissFrequencyRate()));
        row(sb, "Inspection Completion Rate (%)", String.valueOf(m.getInspectionCompletionRate()));
        row(sb, "Corrective Action Closure Rate (%)", String.valueOf(m.getCorrectiveActionClosureRate()));
        row(sb, "Permit Compliance Rate (%)", String.valueOf(m.getPermitComplianceRate()));
        row(sb, "LTIFR", m.getLtifr() == null ? "N/A (requires Story 42)" : String.valueOf(m.getLtifr()));
        row(sb, "TRIFR", m.getTrifr() == null ? "N/A (requires Story 42)" : String.valueOf(m.getTrifr()));
        row(sb, "Regulatory Obligations Overdue",
                m.getRegulatoryObligationsOverdue() == null ? "N/A (requires Story 40)"
                        : String.valueOf(m.getRegulatoryObligationsOverdue()));
        sb.append("</table>");
        if (m.getNote() != null) {
            sb.append("<p style='color:#666;font-size:12px'>").append(m.getNote()).append("</p>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    // ===== core KPI computation =====

    private MetricsResponse computeMetrics(Long siteId, LocalDate from, LocalDate to) {
        long totalIncidents;
        long nearMiss;
        if (siteId != null) {
            totalIncidents = incidentRepository.countBySiteIdAndIncidentDateBetween(siteId, from, to);
            nearMiss = incidentRepository.countBySiteIdAndIncidentTypeAndIncidentDateBetween(
                    siteId, IncidentType.NEAR_MISS, from, to);
        } else {
            totalIncidents = incidentRepository.countByIncidentDateBetween(from, to);
            nearMiss = incidentRepository.countByIncidentTypeAndIncidentDateBetween(
                    IncidentType.NEAR_MISS, from, to);
        }

        long days = Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1);
        double nearMissFreq = round2((double) nearMiss / days * 30.0);

        long totalInspections = inspectionRepository.countByPlannedDateBetween(from, to);
        long completedInspections = inspectionRepository.countByStatusAndPlannedDateBetween(
                InspectionStatus.COMPLETED, from, to);
        double inspectionRate = percent(completedInspections, totalInspections);

        long totalActions = correctiveActionRepository.count();
        long verifiedActions = correctiveActionRepository.countByStatus(CorrectiveActionStatus.VERIFIED);
        double capaRate = percent(verifiedActions, totalActions);

        long totalPermits = permitRepository.count();
        long closedPermits = permitRepository.countByStatus(PermitStatus.CLOSED);
        double permitRate = percent(closedPermits, totalPermits);

        return MetricsResponse.builder()
                .totalIncidents(totalIncidents)
                .nearMissCount(nearMiss)
                .nearMissFrequencyRate(nearMissFreq)
                .inspectionCompletionRate(inspectionRate)
                .correctiveActionClosureRate(capaRate)
                .permitComplianceRate(permitRate)
                .ltifr(null)                          // Story 42
                .trifr(null)                          // Story 42
                .regulatoryObligationsOverdue(null)   // Story 40
                .note(BLOCKED_NOTE)
                .build();
    }

    private double percent(long numerator, long denominator) {
        if (denominator == 0) return 0.0;
        return round2((double) numerator / denominator * 100.0);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private EHSReportResponse toResponse(EHSReport r) {
        MetricsResponse metrics = MetricsResponse.builder()
                .totalIncidents(r.getTotalIncidents())
                .nearMissCount(r.getNearMissCount())
                .nearMissFrequencyRate(r.getNearMissFrequencyRate())
                .inspectionCompletionRate(r.getInspectionCompletionRate())
                .correctiveActionClosureRate(r.getCorrectiveActionClosureRate())
                .permitComplianceRate(r.getPermitComplianceRate())
                .ltifr(r.getLtifr())
                .trifr(r.getTrifr())
                .regulatoryObligationsOverdue(r.getRegulatoryObligationsOverdue())
                .note(BLOCKED_NOTE)
                .build();
        return EHSReportResponse.builder()
                .reportId(r.getReportId())
                .scope(r.getScope())
                .siteId(r.getSiteId())
                .departmentId(r.getDepartmentId())
                .fromDate(r.getFromDate())
                .toDate(r.getToDate())
                .metrics(metrics)
                .generatedDate(r.getGeneratedDate())
                .build();
    }

    private static void row(StringBuilder sb, String label, String value) {
        sb.append("<tr><td>").append(label).append("</td><td>").append(value).append("</td></tr>");
    }
}