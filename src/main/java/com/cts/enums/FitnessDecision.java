package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FitnessDecision {

    FIT_FOR_WORK("FitForWork"),
    FIT_WITH_RESTRICTIONS("FitWithRestrictions"),
    TEMPORARY_UNFIT("TemporaryUnfit"),
    PERMANENTLY_UNFIT("PermanentlyUnfit");

    private final String label;

    FitnessDecision(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static FitnessDecision fromValue(String value) {
        for (FitnessDecision d : values()) {
            if (d.label.equalsIgnoreCase(value) || d.name().equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("Invalid FitnessDecision: " + value);
    }
}