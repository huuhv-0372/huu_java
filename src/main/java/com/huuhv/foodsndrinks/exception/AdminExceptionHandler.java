package com.huuhv.foodsndrinks.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Chỉ bắt lỗi phát sinh từ thư mục admin
@ControllerAdvice(basePackages = "com.huuhv.foodsndrinks.controller.admin")
public class AdminExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAdminException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/admin/500"; // Trỏ về thư mục error/admin/
    }
}
