package com.atp.fwfe.dto.account.reportRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {

    @NotNull
    private Long reportedAccountId;

    @NotNull
    private String reason;
}
