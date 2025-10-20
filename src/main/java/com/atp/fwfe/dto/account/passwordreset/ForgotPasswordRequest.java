package com.atp.fwfe.dto.account.passwordreset;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {

    @Email
    private String email;
}
