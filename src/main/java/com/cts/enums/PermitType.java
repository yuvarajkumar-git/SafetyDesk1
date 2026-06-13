package com.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermitType {

    HOT_WORK("HotWork"),
    CONFINED_SPACE("ConfinedSpace"),
    ELECTRICAL_ISOLATION("ElectricalIsolation"),
    WORK_AT_HEIGHT("WorkAtHeight"),
    EXCAVATION("Excavation"),
    CHEMICAL_HANDLING("ChemicalHandling");

    private final String label;

    PermitType(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static PermitType fromValue(String value) {
        for (PermitType t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid PermitType: " + value);
    }
}