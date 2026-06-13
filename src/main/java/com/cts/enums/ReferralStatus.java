package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReferralStatus {

    REFERRED("Referred"),
    ATTENDED("Attended"),
    FOLLOW_UP_REQUIRED("FollowUpRequired"),
    CLOSED("Closed");

    private final String label;

    ReferralStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static ReferralStatus fromValue(String value) {
        for (ReferralStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid ReferralStatus: " + value);
    }
}