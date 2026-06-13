package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AssessmentType {

    PRE_EMPLOYMENT("PreEmployment"),
    PERIODIC("Periodic"),
    POST_INCIDENT("PostIncident"),
    RETURN_TO_WORK("ReturnToWork"),
    EXIT("Exit");

    private final String label;

    AssessmentType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static AssessmentType fromValue(String value) {
        for (AssessmentType t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid AssessmentType: " + value);
    }
}