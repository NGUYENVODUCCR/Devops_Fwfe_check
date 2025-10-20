package com.atp.fwfe.dto.work;

import com.atp.fwfe.model.account.Account;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCompanyDto {

    @NotBlank(message = "Vui lòng điền tên cơ sở kinh doanh")
    private String name;

    @NotBlank(message = "Giới thiệu cơ bản về cơ sở kinh doanh của bạn")
    private String descriptionCompany;

    @NotBlank(message = "Loại hình kinh doanh của bạn là gì? (vd: tạp hóa, caffee, xưởng may,...")
    private String type;

    @NotBlank(message = "Nhập địa chỉ cơ sở kinh doanh")
    private String address;

    private Account createdBy;

    private Boolean isPublic;

}
