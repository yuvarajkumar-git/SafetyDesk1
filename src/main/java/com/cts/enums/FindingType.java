package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FindingType {

    NON_CONFORMANCE("NonConformance"),
    OBSERVATION("Observation"),
    BEST_PRACTICE("BestPractice");

    private final String label;

    FindingType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static FindingType fromValue(String value) {
        for (FindingType t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid FindingType: " + value);
    }
}