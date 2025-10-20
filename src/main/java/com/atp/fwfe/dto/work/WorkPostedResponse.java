package com.atp.fwfe.dto.work;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkPostedResponse {

    private Long id;
    private String position;
    private String descriptionWork;
    private BigDecimal salary;
    private Integer maxReceiver;
    private int acceptedCount;
    private int maxAccepted;

    private LocalDateTime updatedAt;

    private Long companyId;
    private String companyName;

    private Long createdById;
    private String createdByUsername;

    private boolean isNotified;

    public boolean isNotified() {
        return isNotified;
    }

    public void setIsNotified(boolean isNotified) {
        this.isNotified = isNotified;
    }
}

