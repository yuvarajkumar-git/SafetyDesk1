package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HealthRecordStatus {

    COMPLETED("Completed"),
    PENDING_REVIEW("PendingReview");

    private final String label;

    HealthRecordStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static HealthRecordStatus fromValue(String value) {
        for (HealthRecordStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid HealthRecordStatus: " + value);
    }
}