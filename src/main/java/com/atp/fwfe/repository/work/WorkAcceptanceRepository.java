package com.atp.fwfe.repository.work;

import com.atp.fwfe.model.work.WorkAcceptance;
import com.atp.fwfe.model.work.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkAcceptanceRepository extends JpaRepository<WorkAcceptance, Long> {
    List<WorkAcceptance> findByWorkPostedId(Long workPostedId);
    List<WorkAcceptance> findByAccountId(Long accountId);
    List<WorkAcceptance> findByWorkPostedIdAndAccountIdAndStatus(Long workId, Long accountId, WorkStatus status);


}
