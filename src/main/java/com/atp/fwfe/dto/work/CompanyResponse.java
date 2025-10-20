package com.atp.fwfe.dto.work;

import lombok.Data;

@Data
public class CompanyResponse {
    private Long id;
    private String name;
    private String descriptionCompany;
    private String type;
    private String address;
    private Boolean isPublic;
    private Long createdById;
    private String createdByUsername;
}
