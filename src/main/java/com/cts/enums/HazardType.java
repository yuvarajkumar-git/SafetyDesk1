package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HazardType {

    PHYSICAL("Physical"),
    CHEMICAL("Chemical"),
    BIOLOGICAL("Biological"),
    ERGONOMIC("Ergonomic"),
    PSYCHOSOCIAL("Psychosocial");

    private final String label;

    HazardType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static HazardType fromValue(String value) {
        for (HazardType t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid HazardType: " + value);
    }
}