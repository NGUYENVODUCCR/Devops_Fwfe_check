package com.atp.fwfe.controller.work;

import com.atp.fwfe.dto.work.CreateWorkAcceptanceRequest;
import com.atp.fwfe.dto.work.UpdateWorkAcceptanceStatusRequest;
import com.atp.fwfe.dto.work.WorkAcceptanceResponse;
import com.atp.fwfe.model.work.WorkAcceptance;
import com.atp.fwfe.model.work.WorkStatus;
import com.atp.fwfe.service.account.CustomUserDetails;
import com.atp.fwfe.service.work.WorkAcceptanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/works/{workId}/acceptances")
@CrossOrigin(origins = {
        "https://fwfe.duckdns.org",
        "http://152.42.196.211:3000",
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000",
        "http://localhost:*"
}, allowCredentials = "true")
@RequiredArgsConstructor
public class WorkAcceptanceController {
    private final WorkAcceptanceService acceptanceService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<WorkAcceptanceResponse> accept(
            @PathVariable Long workId,
            @RequestBody @Valid CreateWorkAcceptanceRequest dto,
            @RequestHeader("X-Username") String username) {
        dto.setWorkPostedId(workId);
        WorkAcceptanceResponse resp = acceptanceService.accept(dto, username);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_USER')")
    public ResponseEntity<List<WorkAcceptanceResponse>> getByPost(@PathVariable Long workId) {
        return ResponseEntity.ok(acceptanceService.getByPost(workId));
    }

    @GetMapping("/account/{id}/status/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or #id == #currentUser.id")
    public ResponseEntity<List<WorkAcceptanceResponse>> getByWorkPostedIdAndAccountIdAndStatus(
            @PathVariable(name = "workId") Long workId,
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "status") WorkStatus status,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(acceptanceService.getByWorkPostedIdAndAccountIdAndStatus(workId, id, status));
    }

    @PutMapping("/{acceptanceId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or #username == authentication.name")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long workId,
            @PathVariable Long acceptanceId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody @Valid UpdateWorkAcceptanceStatusRequest dto,
            @RequestHeader("X-Username") String username) {

        acceptanceService.updateStatus(workId, acceptanceId, username, dto.getStatus());
        return ResponseEntity.ok().build();
    }


}
