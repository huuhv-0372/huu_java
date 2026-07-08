package com.huuhv.foodsndrinks.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class ProfileUpdateReqDto {

    @NotBlank(message = "Họ và tên không được để trống.")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự.")
    private String fullName;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Định dạng email không hợp lệ.")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không hợp lệ.")
    private String phone;

    /** Bắt buộc nếu muốn đổi mật khẩu */
    private String currentPassword;

    /** Để trống = không đổi mật khẩu. Độ dài (6-32 ký tự) được kiểm tra ở service — @Size
     *  không dùng được ở đây vì nó coi chuỗi rỗng "" là không hợp lệ, chặn luôn trường hợp bỏ trống. */
    private String newPassword;

    private String confirmNewPassword;
}
