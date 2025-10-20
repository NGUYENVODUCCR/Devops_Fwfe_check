package com.atp.fwfe.dto.work;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateWorkAcceptanceRequest {

    @NotNull(message = "Vui lòng chọn công việc")
    private Long workPostedId;

    @NotNull(message = "Tài khoản không được để trống")
    private Long accountId;
}
