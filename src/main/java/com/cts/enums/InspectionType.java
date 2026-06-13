package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InspectionType {

    ROUTINE("Routine"),
    COMPLIANCE("Compliance"),
    SURPRISE("Surprise"),
    INCIDENT_FOLLOW_UP("IncidentFollow-Up");

    private final String label;

    InspectionType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static InspectionType fromValue(String value) {
        for (InspectionType t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid InspectionType: " + value);
    }
}