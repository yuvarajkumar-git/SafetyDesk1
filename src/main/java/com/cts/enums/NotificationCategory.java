package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationCategory {

    INCIDENT("Incident"),
    PERMIT("Permit"),
    INSPECTION("Inspection"),
    HEALTH("Health"),
    COMPLIANCE("Compliance"),
    CAPA("CAPA");

    private final String label;

    NotificationCategory(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static NotificationCategory fromValue(String value) {
        for (NotificationCategory c : values()) {
            if (c.label.equalsIgnoreCase(value) || c.name().equalsIgnoreCase(value)) return c;
        }
        throw new IllegalArgumentException("Invalid NotificationCategory: " + value);
    }
}