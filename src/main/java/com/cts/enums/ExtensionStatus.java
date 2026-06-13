package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExtensionStatus {

    REQUESTED("Requested"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String label;

    ExtensionStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static ExtensionStatus fromValue(String value) {
        for (ExtensionStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid ExtensionStatus: " + value);
    }
}