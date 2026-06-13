package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Severity {

    MINOR("Minor"),
    MODERATE("Moderate"),
    SERIOUS("Serious"),
    FATAL("Fatal");

    private final String label;

    Severity(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static Severity fromValue(String value) {
        for (Severity s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid Severity: " + value);
    }
}