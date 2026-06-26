package com.huuhv.foodsndrinks.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterReqDto {

    @NotBlank(message = "Họ và tên không được để trống.")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự.")
    private String fullName;

    @NotBlank(message = "Username không được để trống.")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username chỉ được chứa chữ cái, số và các dấu (._-).")
    private String username;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự.")
    private String email;

    // Thêm trường Phone với Regex chuẩn mạng viễn thông VN
    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không hợp lệ (Ví dụ: 0912345678).")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 6, max = 32, message = "Mật khẩu phải từ 6 đến 32 ký tự.")
    private String password;

    @NotBlank(message = "Vui lòng xác nhận lại mật khẩu.")
    private String confirmPassword;
}
