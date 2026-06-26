package com.huuhv.foodsndrinks.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "Các API liên quan đến tìm kiếm và hiển thị sản phẩm")
public class ProductApiController {

    @Operation(
            summary = "Lấy danh sách thức ăn/đồ uống",
            description = "Trả về danh sách sản phẩm, hỗ trợ filter theo type, name..."
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        // Logic here
        return ResponseEntity.ok(java.util.List.of());
    }
}
