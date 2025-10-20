package com.atp.fwfe.controller.account;

import com.atp.fwfe.dto.account.userRequest.UserUpdateRequest;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.service.account.AccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = {
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000"
}, allowCredentials = "true")
public class AccountController {

    @Autowired
    private AccService accService;

    @Autowired
    public AccountController(AccService accService){
        this.accService = accService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request){
        Account updated = accService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER','ROLE_USER')")
    @GetMapping("/search")
    public ResponseEntity<?> searchAccount(@RequestParam String keyword){
        List<Account> results = accService.searchByKeyword(keyword);
        if(results.isEmpty()){
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }
        return ResponseEntity.ok(results);
    }


}
