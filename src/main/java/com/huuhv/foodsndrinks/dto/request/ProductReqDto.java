package com.huuhv.foodsndrinks.dto.request;

import com.huuhv.foodsndrinks.enums.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReqDto {

    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống!")
    private String name;

    @NotNull(message = "Vui lòng chọn danh mục!")
    private Long categoryId;

    @NotNull(message = "Vui lòng chọn loại sản phẩm!")
    private ProductType type;

    @NotNull(message = "Vui lòng nhập giá sản phẩm!")
    @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0!")
    private BigDecimal price;

    private String description;

    private Boolean isAvailable = true;
}

