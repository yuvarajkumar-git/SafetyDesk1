package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentStatus {

    REPORTED("Reported"),
    UNDER_INVESTIGATION("UnderInvestigation"),
    CAPA_ASSIGNED("CAPAAssigned"),
    CLOSED("Closed");

    private final String label;

    IncidentStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static IncidentStatus fromValue(String value) {
        for (IncidentStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid IncidentStatus: " + value);
    }
}