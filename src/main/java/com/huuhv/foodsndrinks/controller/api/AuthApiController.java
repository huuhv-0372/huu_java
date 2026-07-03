package com.huuhv.foodsndrinks.controller.api;

import com.huuhv.foodsndrinks.dto.request.LoginReqDto;
import com.huuhv.foodsndrinks.dto.response.AuthResDto;
import com.huuhv.foodsndrinks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "Xác thực và cấp JWT token")
public class AuthApiController {

    private final AuthService authService;

    @Operation(
            summary = "Đăng nhập lấy JWT token",
            description = "Xác thực bằng username/email + mật khẩu. Token trả về dùng ở header: Authorization: Bearer <token>"
    )
    @ApiResponse(responseCode = "200", description = "Đăng nhập thành công, trả về access token")
    @ApiResponse(responseCode = "400", description = "Thiếu username hoặc mật khẩu")
    @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập hoặc tài khoản bị khóa")
    @PostMapping("/login")
    public ResponseEntity<AuthResDto> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        return ResponseEntity.ok(authService.login(loginReqDto));
    }
}
