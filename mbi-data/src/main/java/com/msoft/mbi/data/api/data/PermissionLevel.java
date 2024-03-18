package com.msoft.mbi.data.api.data;

public enum PermissionLevel {
    READ("READ"),
    WRITE("WRITE");

    public final String label;

    PermissionLevel(String label) {
        this.label = label;
    }
}
