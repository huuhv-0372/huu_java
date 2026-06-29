package com.huuhv.foodsndrinks.controller.admin;

import com.huuhv.foodsndrinks.enums.CategoryType;
import com.huuhv.foodsndrinks.enums.ProductType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.huuhv.foodsndrinks.controller.admin")
public class AdminLayoutModelAdvice {

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request == null ? "" : request.getRequestURI();
    }

    @ModelAttribute("categoryTypes")
    public CategoryType[] categoryTypes() {
        return CategoryType.values();
    }

    @ModelAttribute("productTypes")
    public ProductType[] productTypes() {
        return ProductType.values();
    }
}
