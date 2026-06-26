package com.huuhv.foodsndrinks.enums;

public enum CategoryType {
    FOOD("Đồ ăn"),
    DRINK("Đồ uống"),
    ALL("Tất cả");

    private final String label;

    CategoryType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
