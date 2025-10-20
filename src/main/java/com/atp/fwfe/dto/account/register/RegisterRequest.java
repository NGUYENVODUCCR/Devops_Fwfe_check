package com.atp.fwfe.dto.account.register;

import com.atp.fwfe.dto.work.CreateCompanyDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Mật khẩu phải gồm chữ hoa, chữ thường, số và ký tự đặc biệt. Vì quyền lợi của khách hàng!"
    )
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$",
            message = "Vui lòng sử dụng email @gmail.com"
    )
    private String email;

    @NotBlank(message = "Vui lòng dùng tên thật của bạn!!")
    private String name;

    private CreateCompanyDto company;


    @AssertTrue(message = "Mật khẩu xác nhận không khớp")
    public boolean isPasswordConfirmed() {
        if(password == null || confirmPassword == null) return false;
        return password.equals(confirmPassword);
    }

}
