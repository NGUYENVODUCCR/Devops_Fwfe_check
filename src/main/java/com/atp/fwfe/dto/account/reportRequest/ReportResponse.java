package com.atp.fwfe.dto.account.reportRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    private Long id;
    private String reason;
    private LocalDateTime reportedAt;
    private boolean resolved;
    private Long reporterId;
    private String reporterUsername;
    private Long reportedId;
    private String reportedUsername;
    private boolean reportedLocked;

}

