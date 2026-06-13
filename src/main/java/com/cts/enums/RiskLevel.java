package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskLevel {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String label;

    RiskLevel(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static RiskLevel fromValue(String value) {
        for (RiskLevel r : values()) {
            if (r.label.equalsIgnoreCase(value) || r.name().equalsIgnoreCase(value)) return r;
        }
        throw new IllegalArgumentException("Invalid RiskLevel: " + value);
    }
}