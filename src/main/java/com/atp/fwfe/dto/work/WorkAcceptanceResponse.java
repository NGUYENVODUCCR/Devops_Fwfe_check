package com.atp.fwfe.dto.work;

import com.atp.fwfe.model.work.WorkStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkAcceptanceResponse {

    private Long id;

    private Long accountId;
    private String accountUsername;

    private Long workPostedId;
    private String position;

    private LocalDateTime acceptedAt;

    private WorkStatus status;
    private String companyName;

}

