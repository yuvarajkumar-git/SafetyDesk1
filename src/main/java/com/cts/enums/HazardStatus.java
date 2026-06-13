package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HazardStatus {

    OPEN("Open"),
    MITIGATED("Mitigated"),
    CLOSED("Closed"),
    RECURRING("Recurring");

    private final String label;

    HazardStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static HazardStatus fromValue(String value) {
        for (HazardStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid HazardStatus: " + value);
    }
}