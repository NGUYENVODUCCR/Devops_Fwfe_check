package com.atp.fwfe.dto.work;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateWorkPostedRequest {

    @NotBlank(message = "Vui lòng nhập vị trí công việc")
    private String position;

    @NotBlank(message = "Vui lòng mô tả công việc")
    private String descriptionWork;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương phải lớn hơn 0")
    private BigDecimal salary;

    @NotNull(message = "Số lượng người nhận không được để trống")
    @Min(value = 1, message = "Số lượng người nhận tối thiểu là 1")
    private Integer maxReceiver;

    @NotNull(message = "Công ty không được để trống")
    private Long companyId;

    private Integer maxAccepted = 1;
}

