package com.atp.fwfe.model.work;

import com.atp.fwfe.model.account.Account;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String descriptionCompany;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String address;


    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonBackReference
    private Account createdBy;

    @OneToMany(mappedBy = "company")
    private List<WorkPosted> workPostings;

    @Column(nullable = false)
    private Boolean isPublic = true;

}
