package com.atp.fwfe.controller.account;

import com.atp.fwfe.dto.account.adminRequest.AdminCreateUserRequest;
import com.atp.fwfe.dto.account.adminRequest.AdminUpdateUserRequest;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.service.account.AccService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {
        "https://fwfe.duckdns.org",
        "http://152.42.196.211:3000",
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000",
        "http://localhost:*"
}, allowCredentials = "true")
public class AdminController {

  @Autowired
  private final AccService accService;

  @Autowired
  public AdminController(AccService accService) {
    this.accService = accService;
  }

  //ADMIN
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/create-account")
  public ResponseEntity<String> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
    return accService.createUser(request);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("/lock/{id}")
  public ResponseEntity<?> lockUser(@PathVariable Long id) {
    return accService.lockUser(id);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("/unlock/{id}")
  public ResponseEntity<?> unlockUser(@PathVariable Long id) {
    return accService.unlockUser(id);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request){
    Account updated = accService.updateAdmin(id,request);
    return ResponseEntity.ok(updated);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public List<Account> getAll(){
    return accService.findAll();
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    accService.delete(id);
  }


  // ADMIN và chủ tài khoản
  @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
  @GetMapping("/{id}")
  public Account getOne(@PathVariable Long id){
    return accService.findOne(id);
  }



}
