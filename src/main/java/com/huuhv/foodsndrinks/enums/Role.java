package com.huuhv.foodsndrinks.enums;

public enum Role {
    ROLE_USER ("Người dùng"),
    ROLE_ADMIN("Quản trị viên");

    private final String label;

    Role(String label) { this.label = label; }

    public String getLabel() { return label; }
}
