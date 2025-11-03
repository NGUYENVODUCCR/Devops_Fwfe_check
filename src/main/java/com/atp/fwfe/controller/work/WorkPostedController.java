package com.atp.fwfe.controller.work;

import com.atp.fwfe.dto.work.WorkPostedResponse;
import com.atp.fwfe.service.work.WorkPostedService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/works-posted")
@CrossOrigin(origins = {
        "https://fwfe.duckdns.org",
        "http://152.42.196.211:3000",
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000",
        "http://localhost:*"
}, allowCredentials = "true")
@RequiredArgsConstructor
public class WorkPostedController {
    private final WorkPostedService postService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<WorkPostedResponse> create(
            @RequestBody @Valid com.atp.fwfe.dto.work.CreateWorkPostedRequest dto,
            @RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(postService.create(dto, username));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_USER')")
    public ResponseEntity<List<WorkPostedResponse>> getAll(
            @RequestHeader("X-Role") String role) {
        List<WorkPostedResponse> response = postService.getAll(role);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String json = mapper.writeValueAsString(response);
            System.out.println("[DEBUG] JSON trả về: " + json);
        } catch (JsonProcessingException e) {
            System.err.println("Lỗi khi chuyển object thành JSON: " + e.getMessage());
        }
        return ResponseEntity.ok(postService.getAll(role));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_USER')")
    public ResponseEntity<WorkPostedResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getOne(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<WorkPostedResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid com.atp.fwfe.dto.work.CreateWorkPostedRequest dto,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role) {
        return ResponseEntity.ok(postService.update(id, dto, username, role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role) {
        postService.delete(id, username, role);
        return ResponseEntity.noContent().build();
    }
}

