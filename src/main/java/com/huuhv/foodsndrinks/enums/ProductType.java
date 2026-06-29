package com.huuhv.foodsndrinks.enums;

public enum ProductType {
    FOOD("Đồ ăn"),
    DRINK("Đồ uống");

    private final String label;

    ProductType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
