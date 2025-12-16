package com.atp.fwfe.dto.account.adminRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {

    private String email;
    private String role;
    private boolean locked;
    private String updatedBy;
    private String name;
}
