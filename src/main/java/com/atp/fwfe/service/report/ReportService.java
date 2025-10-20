package com.atp.fwfe.service.report;

import com.atp.fwfe.dto.account.reportRequest.ReportRequest;
import com.atp.fwfe.dto.account.reportRequest.ReportResponse;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.report.Report;
import com.atp.fwfe.repository.account.AccRepository;
import com.atp.fwfe.repository.report.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepo;
    private final AccRepository accountRepo;

    public void createReport(String reporterUsername, ReportRequest request) {
        Account reporter = accountRepo.findByUsername(reporterUsername)
                .orElseThrow(() -> new EntityNotFoundException("Người gửi không tồn tại"));

        Account reported = accountRepo.findById(request.getReportedAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Người bị báo cáo không tồn tại"));

        Report report = new Report();
        report.setReporter(reporter);
        report.setReported(reported);
        report.setReason(request.getReason());
        report.setResolved(false);

        reportRepo.save(report);
    }

    public List<Report> findByResolvedFalse() {
        return reportRepo.findByResolvedFalse();
    }

    public ReportResponse mapToResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reason(report.getReason())
                .reportedAt(report.getReportedAt())
                .resolved(report.isResolved())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reportedId(report.getReported().getId())
                .reportedUsername(report.getReported().getUsername())
                .reportedLocked(report.getReported().isLocked())
                .build();
    }

}
