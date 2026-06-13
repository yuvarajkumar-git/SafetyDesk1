package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentType {

    INJURY("Injury"),
    NEAR_MISS("NearMiss"),
    PROPERTY_DAMAGE("PropertyDamage"),
    ENVIRONMENTAL_RELEASE("EnvironmentalRelease"),
    UNSAFE_ACT("UnsafeAct"),
    UNSAFE_CONDITION("UnsafeCondition");

    private final String label;

    IncidentType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static IncidentType fromValue(String value) {
        for (IncidentType t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid IncidentType: " + value);
    }
}