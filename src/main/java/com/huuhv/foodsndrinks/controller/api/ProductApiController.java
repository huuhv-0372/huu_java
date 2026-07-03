package com.huuhv.foodsndrinks.controller.api;

import com.huuhv.foodsndrinks.dto.response.PageResDto;
import com.huuhv.foodsndrinks.dto.response.ProductResDto;
import com.huuhv.foodsndrinks.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Các API liên quan đến tìm kiếm và hiển thị sản phẩm")
public class ProductApiController {

    private final ProductService productService;

    @Operation(
            summary = "Lấy danh sách thức ăn/đồ uống",
            description = "Trả về danh sách sản phẩm đang kinh doanh (phân trang), hỗ trợ filter theo name, categoryId, type (FOOD/DRINK)"
    )
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    @GetMapping
    public ResponseEntity<PageResDto<ProductResDto>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);
        // Public listing only exposes products still on sale → isAvailable = "true"
        Page<ProductResDto> products = productService.searchProducts(name, categoryId, type, "true", safePage, safeSize);
        return ResponseEntity.ok(PageResDto.from(products));
    }

    @Operation(
            summary = "Lấy chi tiết sản phẩm",
            description = "Trả về thông tin chi tiết sản phẩm kèm danh sách hình ảnh"
    )
    @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResDto> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }
}
