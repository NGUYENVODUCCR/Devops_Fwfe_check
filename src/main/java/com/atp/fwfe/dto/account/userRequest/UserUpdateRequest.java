package com.atp.fwfe.dto.account.userRequest;
import com.atp.fwfe.dto.work.CreateCompanyDto;
import com.atp.fwfe.model.work.Company;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
    private CreateCompanyDto company;
    private String updatedBy;
}
