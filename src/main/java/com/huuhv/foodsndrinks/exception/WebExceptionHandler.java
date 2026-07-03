package com.huuhv.foodsndrinks.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
// Chỉ bắt lỗi phát sinh từ thư mục web
@ControllerAdvice(basePackages = "com.huuhv.foodsndrinks.controller.web")
public class WebExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        log.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorView("error/404", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ModelAndView handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        log.warn("Resource conflict at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorView("error/409", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("An unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorView("error/500", "Đã có lỗi xảy ra, vui lòng thử lại sau.", request.getRequestURI());
    }

    private ModelAndView buildErrorView(String viewName, String message, String path) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("message", message);
        modelAndView.addObject("path", path);
        return modelAndView;
    }
}
