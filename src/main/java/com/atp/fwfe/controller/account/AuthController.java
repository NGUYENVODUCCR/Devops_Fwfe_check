package com.atp.fwfe.controller.account;

import com.atp.fwfe.dto.account.login.LoginRequest;
import com.atp.fwfe.dto.account.login.LoginResponse;
import com.atp.fwfe.dto.account.register.RegisterRequest;
import com.atp.fwfe.service.account.AccService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000"
}, allowCredentials = "true")
public class AuthController {

    private final AccService accService;

    public AuthController(AccService accService) {
        this.accService = accService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return accService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return accService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        return accService.logout(token);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        return accService.validateToken(token);
    }

}
