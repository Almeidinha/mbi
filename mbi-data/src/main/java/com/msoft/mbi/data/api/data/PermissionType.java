package com.msoft.mbi.data.api.data;

public enum PermissionType {
    USER("USER"),
    GROUP("GROUP");

    public final String label;

    PermissionType(String label) {
        this.label = label;
    }
}
