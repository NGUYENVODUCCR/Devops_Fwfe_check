package com.atp.fwfe.service.work;
import com.atp.fwfe.dto.work.*;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.work.Company;
import com.atp.fwfe.model.work.WorkPosted;
import com.atp.fwfe.repository.account.AccRepository;
import com.atp.fwfe.repository.work.CompanyRepository;
import com.atp.fwfe.repository.work.WorkPostedRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkPostedService {
    private final WorkPostedRepository postRepo;
    private final AccRepository accountRepo;
    private final CompanyRepository companyRepo;

    public WorkPostedResponse create(CreateWorkPostedRequest dto, String username) {
        Account creator = accountRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        Company comp = companyRepo.findById(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công ty"));
        WorkPosted post = WorkPosted.builder()
                .position(dto.getPosition())
                .descriptionWork(dto.getDescriptionWork())
                .salary(dto.getSalary())
                .maxReceiver(dto.getMaxReceiver())
                .maxAccepted(dto.getMaxAccepted() != null ? dto.getMaxAccepted() : 1)
                .createdBy(creator)
                .company(comp)
                .build();
        WorkPosted saved = postRepo.save(post);
        return mapToResponse(saved);
    }

    public List<WorkPostedResponse> getAll(String role) {
        List<WorkPosted> posts = postRepo.findAllWithRelations();
        return posts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }


    public WorkPostedResponse getOne(Long id) {
        WorkPosted post = postRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng"));
        return mapToResponse(post);
    }

    public WorkPostedResponse update(Long id, CreateWorkPostedRequest dto, String username, String role) {
        WorkPosted post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng"));
        if ("ROLE_MANAGER".equals(role) && !post.getCreatedBy().getUsername().equals(username)) {
            throw new SecurityException("Quyền truy cập bị từ chối!");
        }
        post.setPosition(dto.getPosition());
        post.setDescriptionWork(dto.getDescriptionWork());
        post.setSalary(dto.getSalary());
        post.setMaxReceiver(dto.getMaxReceiver());
        post.setMaxAccepted(dto.getMaxAccepted());
        return mapToResponse(postRepo.save(post));
    }

    public void delete(Long id, String username, String role) {
        WorkPosted post = postRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng"));
        if ("ROLE_MANAGER".equals(role) && !post.getCreatedBy().getUsername().equals(username)) {
            throw new SecurityException("Quyền truy cập bị từ chối!");
        }
        postRepo.delete(post);
    }

    public List<WorkPosted> findUnnotified() {
        return postRepo.findByIsNotifiedFalse();
    }

    public void markAsNotified(List<Long> ids) {
        ids.forEach(id -> {
            postRepo.findById(id).ifPresent(post -> {
                post.setNotified(true);
                postRepo.save(post);
            });
        });
    }

    private WorkPostedResponse mapToResponse(WorkPosted p) {
        WorkPostedResponse r = new WorkPostedResponse();
        r.setId(p.getId());
        r.setPosition(p.getPosition());
        r.setDescriptionWork(p.getDescriptionWork());
        r.setSalary(p.getSalary());
        r.setMaxReceiver(p.getMaxReceiver());
        r.setAcceptedCount(p.getAcceptedCount());
        r.setMaxAccepted(p.getMaxAccepted());
        r.setIsNotified(p.isNotified());
        r.setUpdatedAt(p.getUpdatedAt());
        r.setCompanyId(p.getCompany().getId());
        r.setCompanyName(p.getCompany().getName());
        r.setCreatedById(p.getCreatedBy().getId());
        r.setCreatedByUsername(p.getCreatedBy().getUsername());
        return r;
    }
}

