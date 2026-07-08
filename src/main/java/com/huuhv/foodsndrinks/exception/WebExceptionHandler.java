package com.huuhv.foodsndrinks.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Chỉ bắt lỗi phát sinh từ thư mục web
@ControllerAdvice(basePackages = "com.huuhv.foodsndrinks.controller.web")
public class WebExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleWebException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/500"; // Trỏ về thư mục error/ (bên ngoài)
    }
}
