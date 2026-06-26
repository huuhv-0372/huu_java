package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.dto.request.RegisterReqDto;
import com.huuhv.foodsndrinks.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "web/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerReqDto", new RegisterReqDto());

        return "web/register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("registerReqDto") RegisterReqDto registerReqDto,
                                      BindingResult bindingResult,
                                      Model model) {

        model.addAttribute("oldFullName", registerReqDto.getFullName());
        model.addAttribute("oldUsername", registerReqDto.getUsername());
        model.addAttribute("oldEmail", registerReqDto.getEmail());
        model.addAttribute("oldPhone", registerReqDto.getPhone());

        // Check password vs confirm password
        if (registerReqDto.getPassword() != null && !registerReqDto.getPassword().equals(registerReqDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerReqDto", "Mật khẩu xác nhận không khớp!");
        }

        if (bindingResult.hasErrors()) {
            return "web/register";
        }

        if (userService.existsByUsername(registerReqDto.getUsername())) {
            bindingResult.rejectValue("username", "error.registerDto", "Username này đã có người sử dụng.");
        }

        if (userService.existsByEmail(registerReqDto.getEmail())) {
            bindingResult.rejectValue("email", "error.registerDto", "Email này đã được sử dụng bởi tài khoản khác.");
        }

        if (userService.existsByPhone(registerReqDto.getPhone())) {
            bindingResult.rejectValue("phone", "error.registerDto", "Số điện thoại này đã được sử dụng bởi tài khoản khác.");
        }

        if (bindingResult.hasErrors()) {
            return "web/register";
        }

        try {
            userService.registerNewUser(registerReqDto);
        } catch (Exception ex) {
            log.error("Registration failed for username={}", registerReqDto.getUsername(), ex);
            model.addAttribute("errorMsg", "Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại!");
            return "web/register";
        }

        return "redirect:/login"; // Redirect to login page after successful registration
    }
}
