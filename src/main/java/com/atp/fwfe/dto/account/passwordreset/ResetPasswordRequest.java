package com.atp.fwfe.dto.account.passwordreset;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    private String email;
    private String code;
    private String newPassword;
}
