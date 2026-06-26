package com.huuhv.foodsndrinks.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"", "/"})
public class HomeController {
    @GetMapping()
    public String home() {
        return "web/index"; // Return the name of the Thymeleaf template (list.html)
    }
}
