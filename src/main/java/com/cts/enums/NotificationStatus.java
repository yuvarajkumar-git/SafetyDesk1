package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationStatus {

    UNREAD("Unread"),
    READ("Read"),
    DISMISSED("Dismissed");

    private final String label;

    NotificationStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static NotificationStatus fromValue(String value) {
        for (NotificationStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid NotificationStatus: " + value);
    }
}