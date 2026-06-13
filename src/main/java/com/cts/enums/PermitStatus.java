package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermitStatus {

    DRAFT("Draft"),
    PENDING_APPROVAL("PendingApproval"),
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    CLOSED("Closed"),
    EXPIRED("Expired");

    private final String label;

    PermitStatus(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static PermitStatus fromValue(String value) {
        for (PermitStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid PermitStatus: " + value);
    }
}