package com.atp.fwfe.service.work;
import com.atp.fwfe.dto.work.*;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.work.WorkPosted;
import com.atp.fwfe.model.work.WorkAcceptance;
import com.atp.fwfe.model.work.WorkStatus;
import com.atp.fwfe.repository.account.AccRepository;
import com.atp.fwfe.repository.work.WorkPostedRepository;
import com.atp.fwfe.repository.work.WorkAcceptanceRepository;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkAcceptanceService {
    private final WorkAcceptanceRepository accRepo;
    private final WorkPostedRepository postRepo;
    private final AccRepository accountRepo;

    @Transactional
    public WorkAcceptanceResponse accept(CreateWorkAcceptanceRequest dto, String username) {
        Account user = accountRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản?"));
        WorkPosted post = postRepo.findById(dto.getWorkPostedId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng?"));
        if (post.getAcceptedCount() >= post.getMaxAccepted()) {
            throw new IllegalStateException("Công việc này đã đủ người!");
        }
        boolean exists = post.getAcceptances().stream()
                .anyMatch(a -> a.getAccount().getId().equals(user.getId()));
        if (exists) {
            throw new IllegalStateException("Đã nhận việc");
        }
        WorkAcceptance acc = WorkAcceptance.builder()
                .workPosted(post)
                .account(user)
                .build();
        post.setAcceptedCount(post.getAcceptedCount() + 1);
        post.getAcceptances().add(acc);
        WorkAcceptance saved = accRepo.save(acc);
        return mapToResponse(saved);
    }

    public List<WorkAcceptanceResponse> getByPost(Long postId) {
        return accRepo.findByWorkPostedId(postId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<WorkAcceptanceResponse> getByWorkPostedIdAndAccountIdAndStatus(Long workId, Long accountId, WorkStatus status) {
        return accRepo.findByWorkPostedIdAndAccountIdAndStatus(workId, accountId, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long workId, Long acceptanceId, String username, WorkStatus newStatus) {
        Account user = accountRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        WorkAcceptance acc = accRepo.findById(acceptanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy việc đã nhận"));

        if (!acc.getWorkPosted().getId().equals(workId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Công việc không khớp");
        }

        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());
        boolean isOwner = acc.getAccount().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chính chủ mới được cập nhật");
        }

        if (acc.getStatus() == WorkStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COMPLETED");
        }

        if (acc.getStatus() == WorkStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CANCELLED");
        }

        acc.setStatus(newStatus);

        if (newStatus == WorkStatus.CANCELLED) {
            WorkPosted post = acc.getWorkPosted();
            post.setAcceptedCount(post.getAcceptedCount() - 1);
        }

        accRepo.save(acc);
    }


    private WorkAcceptanceResponse mapToResponse(WorkAcceptance a) {
        WorkAcceptanceResponse r = new WorkAcceptanceResponse();
        r.setId(a.getId());
        r.setAccountId(a.getAccount().getId());
        r.setAccountUsername(a.getAccount().getUsername());
        r.setWorkPostedId(a.getWorkPosted().getId());
        r.setPosition(a.getWorkPosted().getPosition());
        r.setAcceptedAt(a.getAcceptedAt());
        r.setStatus(a.getStatus());
        r.setCompanyName(a.getWorkPosted().getCompany().getName());
        return r;
    }
}

