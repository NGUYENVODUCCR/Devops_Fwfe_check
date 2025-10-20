package com.atp.fwfe.controller.report;

import com.atp.fwfe.dto.account.reportRequest.ReportRequest;
import com.atp.fwfe.dto.account.reportRequest.ReportResponse;
import com.atp.fwfe.model.report.Report;
import com.atp.fwfe.service.report.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> reportUser(
            @RequestHeader("X-Username") String reporterUsername,
            @RequestBody @Valid ReportRequest request
    ) {
        reportService.createReport(reporterUsername, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unresolved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getUnresolvedReports() {
        List<Report> reports = reportService.findByResolvedFalse();
        List<ReportResponse> response = reports.stream()
                .map(reportService::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

}
