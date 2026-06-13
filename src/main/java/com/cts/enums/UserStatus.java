package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {

    ACTIVE("Active"),
    INACTIVE("Inactive"),
    TRANSFERRED("Transferred");

    private final String label;

    UserStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static UserStatus fromValue(String value) {
        for (UserStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid UserStatus: " + value);
    }
}