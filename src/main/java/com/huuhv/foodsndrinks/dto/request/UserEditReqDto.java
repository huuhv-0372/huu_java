package com.huuhv.foodsndrinks.dto.request;

import com.huuhv.foodsndrinks.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEditReqDto {

    private Long id;

    @NotBlank(message = "Họ và tên không được để trống.")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự.")
    private String fullName;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không hợp lệ.")
    private String phone;

    @NotNull(message = "Vui lòng chọn vai trò.")
    private Role role;

    private Boolean isActive = true;

    /** Để trống = không đổi mật khẩu */
    @Size(min = 6, max = 32, message = "Mật khẩu phải từ 6 đến 32 ký tự.")
    private String newPassword;
}

