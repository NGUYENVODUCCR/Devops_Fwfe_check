package com.atp.fwfe.dto.account.adminRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {

    // username sẽ do người dùng tự đặt, password sẽ do người dùng quên mật khẩu -> đặt lại
    private String email;
    private String role;
    private boolean locked;
    private String updatedBy;
    private String name;
}
