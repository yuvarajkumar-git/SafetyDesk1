package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InvestigationStatus {

    IN_PROGRESS("InProgress"),
    COMPLETED("Completed"),
    PENDING_APPROVAL("PendingApproval");

    private final String label;

    InvestigationStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static InvestigationStatus fromValue(String value) {
        for (InvestigationStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid InvestigationStatus: " + value);
    }
}