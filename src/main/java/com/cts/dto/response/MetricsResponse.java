package com.cts.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * The Story 23 KPI bundle. Fields blocked by out-of-scope stories
 * (LTIFR/TRIFR -> Story 42 man-hours, RegulatoryObligationsOverdue -> Story 40)
 * are returned as null with an explanatory note.
 */
@Data
@Builder
public class MetricsResponse {

    private long totalIncidents;
    private long nearMissCount;
    private double nearMissFrequencyRate;
    private double inspectionCompletionRate;     // percent
    private double correctiveActionClosureRate;  // percent
    private double permitComplianceRate;         // percent

    // Blocked by out-of-scope dependencies -> null
    private Double ltifr;                          // requires Story 42 (man-hours)
    private Double trifr;                          // requires Story 42 (man-hours)
    private Long regulatoryObligationsOverdue;     // requires Story 40 (obligations)

    private String note;   // explains the null metrics
}