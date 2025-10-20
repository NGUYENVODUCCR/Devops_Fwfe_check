package com.atp.fwfe.repository.work;

import com.atp.fwfe.model.work.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByCreatedById(Long createdById);

    @Query("""
            SELECT c FROM Company c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.descriptionCompany) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.type) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Company> searchAllFields(String keyword);

}
