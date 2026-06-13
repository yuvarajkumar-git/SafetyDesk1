package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CorrectiveActionStatus {

    OPEN("Open"),
    IN_PROGRESS("InProgress"),
    COMPLETED("Completed"),
    OVERDUE("Overdue"),
    VERIFIED("Verified");

    private final String label;

    CorrectiveActionStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static CorrectiveActionStatus fromValue(String value) {
        for (CorrectiveActionStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid CorrectiveActionStatus: " + value);
    }
}