package com.atp.fwfe.model.work;

import com.atp.fwfe.model.account.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "works_acceptance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkAcceptance     {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "work_posted_id", nullable = false)
    private WorkPosted workPosted;

    @Column
    @CreationTimestamp
    private LocalDateTime acceptedAt;

    @Enumerated(EnumType.STRING)
    private WorkStatus status;

    @PrePersist
    public void prePersist(){
        this.acceptedAt = LocalDateTime.now();
        this.status = WorkStatus.PENDING;
    }

}
