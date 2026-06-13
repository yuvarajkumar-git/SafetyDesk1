package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskAssessmentStatus {

    DRAFT("Draft"),
    APPROVED("Approved"),
    SUPERSEDED("Superseded");

    private final String label;

    RiskAssessmentStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static RiskAssessmentStatus fromValue(String value) {
        for (RiskAssessmentStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid RiskAssessmentStatus: " + value);
    }
}