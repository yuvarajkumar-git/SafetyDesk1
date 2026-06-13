package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * User roles. Story value spellings (e.g. "SafetyOfficer") are preserved
 * for the API via the label field, while Java constants follow UPPER_SNAKE_CASE.
 */
public enum Role {

    EMPLOYEE("Employee"),
    SAFETY_OFFICER("SafetyOfficer"),
    PTW_COORDINATOR("PTWCoordinator"),
    OH_NURSE("OHNurse"),
    EHS_MANAGER("EHSManager"),
    COMPLIANCE_OFFICER("ComplianceOfficer"),
    ADMIN("Admin");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    // Tells Jackson: when writing JSON, output this label (e.g. "SafetyOfficer")
    @JsonValue
    public String getLabel() {
        return label;
    }

    // Tells Jackson: when reading JSON, accept either the label OR the enum name
    @JsonCreator
    public static Role fromValue(String value) {
        for (Role role : values()) {
            if (role.label.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid Role: " + value);
    }
}