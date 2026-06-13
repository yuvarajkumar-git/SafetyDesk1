package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InspectionStatus {

    SCHEDULED("Scheduled"),
    COMPLETED("Completed"),
    MISSED("Missed"),
    RESCHEDULED("Rescheduled");

    private final String label;

    InspectionStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static InspectionStatus fromValue(String value) {
        for (InspectionStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid InspectionStatus: " + value);
    }
}