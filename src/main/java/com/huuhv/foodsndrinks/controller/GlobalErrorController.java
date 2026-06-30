package com.huuhv.foodsndrinks.controller;

import com.huuhv.foodsndrinks.dto.response.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, Model model) {
        // Lấy status code của lỗi (ví dụ: 404, 403, 500)
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        // Lấy đường dẫn gốc mà người dùng vừa truy cập gây ra lỗi
        String originalUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (originalUri == null) {
            originalUri = "/";
        }

        // ==========================================
        // 1. Nếu lỗi xuất phát từ API -> Trả về JSON
        // ==========================================
        if (originalUri.startsWith("/api/")) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(statusCode)
                    .error(HttpStatus.valueOf(statusCode).getReasonPhrase())
                    .message("An error occurred")
                    .path(originalUri)
                    .build();
            // Cần trả về ResponseEntity, hàm này trả Object nên Spring tự hiểu
            return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
        }

        // ==========================================
        // 2. Nếu lỗi xuất phát từ Admin -> Trả về view Admin
        // ==========================================
        if (originalUri.startsWith("/admin/")) {
            if (statusCode == 404) return "error/admin/404";
            if (statusCode == 403) return "error/admin/403";
            if (statusCode == 405) return "error/admin/405";
            if (statusCode == 409) return "error/admin/409";

            return "error/admin/500";
        }

        // ==========================================
        // 3. Còn lại mặc định là Web User -> Trả về view Web
        // ==========================================
        if (statusCode == 404) return "error/404";
        if (statusCode == 403) return "error/403";
        if (statusCode == 405) return "error/405";
        if (statusCode == 409) return "error/409";

        return "error/500";
    }
}
