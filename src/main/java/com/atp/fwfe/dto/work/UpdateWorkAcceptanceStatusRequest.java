package com.atp.fwfe.dto.work;

import com.atp.fwfe.model.work.WorkAcceptance;
import com.atp.fwfe.model.work.WorkStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateWorkAcceptanceStatusRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private WorkStatus status;
}

