package com.huuhv.foodsndrinks.exception;

import com.huuhv.foodsndrinks.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Chỉ bắt lỗi phát sinh từ thư mục api.
// @RestControllerAdvice tự động chuyển output thành JSON
@RestControllerAdvice(basePackages = "com.huuhv.foodsndrinks.controller.api")
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.huuhv.foodsndrinks.dto.response.ErrorResponse> handleApiException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
