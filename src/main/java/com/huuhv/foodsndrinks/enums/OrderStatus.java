package com.huuhv.foodsndrinks.enums;

public enum OrderStatus {
    CART        ("Giỏ hàng",       "bg-secondary"),
    PENDING     ("Chờ xác nhận",   "bg-warning text-dark"),
    PROCESSING  ("Đang xử lý",     "bg-info text-dark"),
    COMPLETED   ("Hoàn thành",     "bg-success"),
    CANCELLED   ("Đã hủy",         "bg-danger");

    private final String label;
    private final String badgeClass;

    OrderStatus(String label, String badgeClass) {
        this.label      = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel()      { return label; }
    public String getBadgeClass() { return badgeClass; }
}
