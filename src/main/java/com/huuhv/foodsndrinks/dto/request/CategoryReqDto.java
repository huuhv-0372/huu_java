package com.huuhv.foodsndrinks.dto.request;

import com.huuhv.foodsndrinks.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryReqDto {

    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống.")
    private String name;

    @NotNull(message = "Vui long chọn loại danh mục")
    private CategoryType type;

    private String description;
}
