package com.atp.fwfe.repository.work;


import com.atp.fwfe.model.work.WorkPosted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkPostedRepository extends JpaRepository<WorkPosted, Long> {
    List<WorkPosted> findByCreatedById(Long accountId);
    List<WorkPosted> findByIsNotifiedFalse();

    @Query("SELECT w FROM WorkPosted w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.acceptances a LEFT JOIN FETCH a.account")
    List<WorkPosted> findAllWithRelations();

    @Query("SELECT w FROM WorkPosted w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.company LEFT JOIN FETCH w.acceptances a LEFT JOIN FETCH a.account WHERE w.id = :id")
    Optional<WorkPosted> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT w FROM WorkPosted w JOIN w.acceptances a WHERE a.account.id = :accountId")
    List<WorkPosted> findByAcceptedById(@Param("accountId") Long accountId);

}
