package com.atp.fwfe.model.report;

import com.atp.fwfe.model.account.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account reporter;

    @ManyToOne
    private Account reported;

    private String reason;

    private LocalDateTime reportedAt;

    private boolean resolved = false;

    @PrePersist
    public void prePersist(){
        this.reportedAt = LocalDateTime.now();
    }
}
