package com.atp.fwfe.model.work;

import com.atp.fwfe.model.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "works_posted")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkPosted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String position;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descriptionWork;

    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private Integer maxReceiver;

    @Column(nullable = false)
    private int acceptedCount = 0;

    @Column(nullable = false)
    private int maxAccepted = 1;

    @Column(nullable = false)
    private boolean isNotified = false;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @JsonIgnore
    private Account createdBy;

    @OneToMany(mappedBy = "workPosted", cascade = CascadeType.ALL)
    private List<WorkAcceptance> acceptances;

    @ManyToOne
    @JoinColumn(name = "company", nullable = false)
    private Company company;
}
