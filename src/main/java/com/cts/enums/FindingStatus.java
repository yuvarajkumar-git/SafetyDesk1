package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FindingStatus {

    OPEN("Open"),
    IN_PROGRESS("InProgress"),
    CLOSED("Closed"),
    OVERDUE("Overdue");

    private final String label;

    FindingStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static FindingStatus fromValue(String value) {
        for (FindingStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid FindingStatus: " + value);
    }
}