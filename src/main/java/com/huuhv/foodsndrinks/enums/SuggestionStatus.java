package com.huuhv.foodsndrinks.enums;

public enum SuggestionStatus {
    PENDING  ("Chờ xử lý",    "bg-warning text-dark"),
    REVIEWED ("Đã xem xét",   "bg-info text-dark"),
    APPROVED ("Đã duyệt",     "bg-success"),
    REJECTED ("Đã từ chối",   "bg-danger");

    private final String label;
    private final String badgeClass;

    SuggestionStatus(String label, String badgeClass) {
        this.label      = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel()      { return label; }
    public String getBadgeClass() { return badgeClass; }
}
