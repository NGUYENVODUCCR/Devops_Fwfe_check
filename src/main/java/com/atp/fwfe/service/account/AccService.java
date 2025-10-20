package com.atp.fwfe.service.account;

import com.atp.fwfe.dto.account.adminRequest.AdminCreateUserRequest;
import com.atp.fwfe.dto.account.adminRequest.AdminUpdateUserRequest;
import com.atp.fwfe.dto.account.login.LoginRequest;
import com.atp.fwfe.dto.account.login.LoginResponse;
import com.atp.fwfe.dto.account.register.RegisterRequest;
import com.atp.fwfe.dto.account.userRequest.UserUpdateRequest;
import com.atp.fwfe.dto.work.CreateCompanyDto;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.work.Company;
import com.atp.fwfe.repository.account.AccRepository;
import com.atp.fwfe.repository.report.ReportRepository;
import com.atp.fwfe.security.JwtUtil;
import com.atp.fwfe.service.mailer.MailService;
import com.atp.fwfe.service.work.CompanyService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccService {

    private final AccRepository accRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final ReportRepository reportRepository;
    private final MailService mailService;
    private final CompanyService companyService;

    public AccService(AccRepository accRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, TokenBlacklistService tokenBlacklistService, ReportRepository reportRepository, MailService mailService, CompanyService companyService) {
        this.accRepository = accRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.reportRepository = reportRepository;
        this.mailService = mailService;
        this.companyService = companyService;
    }

    //--------------------------------------------------------------------------------------------
//AUTH chung chung bắt đầu
    public ResponseEntity<String> register(RegisterRequest request) {
        if (accRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username đã tồn tại!");
        }

        if (accRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        Account account = new Account(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getName(),
                "ROLE_USER"
        );

        Account saved = accRepository.save(account);

        if (saved.getEmail() != null && saved.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            try {
                mailService.sendWelcomeEmail(saved.getEmail(), saved.getName());
            } catch (MessagingException e) {
                System.err.println("Lỗi gửi email chào mừng: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Đã đăng ký thành công tài khoản cho: " + request.getUsername());
    }

    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            Account account = accRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

            if (account.isLocked()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(
                                null,
                                "Tài khoản đã bị tạm khóa 1 tuần bởi admin do vi phạm tiêu chuẩn độ tin cậy với cộng đồng.\nVui lòng chờ hoặc liên hệ quản trị viên qua zalo - 0768471834 -",
                                null,
                                account.getUsername(),
                                account.getId()
                        ));
            }

            String token = jwtUtil.generateToken(account.getUsername(), account.getRole());

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    "Đăng nhập thành công!",
                    account.getRole(),
                    account.getUsername(),
                    account.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "Tên đăng nhập hoặc mật khẩu không đúng!", null, null, null));
        }
    }

    public ResponseEntity<String> logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok("Bạn đã đăng xuất tài khoản!");
    }

    public ResponseEntity<String> validateToken(String token) {
        token = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if (username == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ!");
        }

        return ResponseEntity.ok("Token hợp lệ. Role: " + role);
    }



    //AUTH chung chung kết thúc
//----------------------------------------------------------------------------------------------------
//ADMIN bắt đầu-------------------- ADMIN ---------------------------
    public ResponseEntity<String> createUser(AdminCreateUserRequest request) {
        if (accRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username đã tồn tại!");
        }

        if (accRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp?");
        }

        Account account = new Account();
        account.setName(request.getName());
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(request.getRole());

        Account saved = accRepository.save(account);

        if ("ROLE_MANAGER".equals(request.getRole())) {
            CreateCompanyDto companyDto = request.getCompany();
            if (companyDto == null) {
                return ResponseEntity.badRequest().body("Vai trò ROLE_MANAGER yêu cầu phải có đầy đủ thông tin công ty.");
            }
            companyDto.setCreatedBy(account);
            companyService.create(companyDto, account.getUsername());
        }

        if (saved.getEmail() != null && saved.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            try {
                mailService.sendWelcomeEmail(saved.getEmail(), saved.getName());
            } catch (MessagingException e) {
                System.err.println("Lỗi gửi email chào mừng: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Đã đăng ký thành công tài khoản cho: " + request.getUsername());
    }

    public ResponseEntity<?> lockUser(Long id) {
        Optional<Account> optional = accRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng.");
        }

        Account account = optional.get();
        if (account.isLocked()) {
            return ResponseEntity.badRequest().body("Tài khoản đã bị khóa!");
        }

        account.setLocked(true);
        accRepository.save(account);

        return ResponseEntity.ok("Đã khóa tài khoản thành công!");
    }

    public ResponseEntity<?> unlockUser(Long id) {
        Optional<Account> optional = accRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản.");
        }

        Account account = optional.get();
        if (!account.isLocked()) {
            return ResponseEntity.badRequest().body("Tài khoản đang mở!");
        }

        account.setLocked(false);
        accRepository.save(account);

        return ResponseEntity.ok("Đã mở khóa tài khoản thành công!");
    }

    public Account updateAdmin(Long id, AdminUpdateUserRequest request) {
        Account account = findOne(id);

        if (request.getName() != null) {
            account.setName(request.getName());
        }

        if (request.getEmail() != null) {
            Optional<Account> existingEmail = accRepository.findByEmail(request.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác.");
            }
            account.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            account.setRole(request.getRole());
        }

        account.setLocked(request.isLocked());
        account.setUpdatedBy(request.getUpdatedBy());

        return accRepository.save(account);
    }

    public void delete(Long id) {
        accRepository.deleteById(id);
    }


    //ADMIN kết thúc
//------------------------------------------------------------------------------------------------------
//Account(USER + MANAGER) bắt đầu
    public List<Account> findAll() {
        return accRepository.findAll();
    }

    public Account findOne(Long id) {
        return accRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
    }

    public Account updateUser(Long id, UserUpdateRequest request) {
        Account account = findOne(id);

        if (request.getUsername() != null && !request.getUsername().equals(account.getUsername())) {
            Optional<Account> existingUsername = accRepository.findByUsername(request.getUsername());
            if (existingUsername.isPresent()) {
                throw new IllegalArgumentException("Username đã tồn tại trong hệ thống.");
            }
            account.setUsername(request.getUsername());
        }

        if (request.getName() != null) account.setName(request.getName());

        if (request.getEmail() != null && !request.getEmail().equals(account.getEmail())) {
            Optional<Account> existingEmail = accRepository.findByEmail(request.getEmail());
            if (existingEmail.isPresent()) {
                throw new IllegalArgumentException("Email đã dùng để đăng ký tài khoản khác trong hệ thống. Vui lòng chọn Email khác!");
            }
            account.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!!");
            }
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if ("ROLE_MANAGER".equals(request.getRole()) && request.getCompany() != null) {
            Company company = convertDtoToCompany(request.getCompany(), account);
            account.setCompany(List.of(company));
        }

        account.setUpdatedBy(request.getUpdatedBy());
        return accRepository.save(account);
    }

    public Company convertDtoToCompany(CreateCompanyDto dto, Account creator) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setDescriptionCompany(dto.getDescriptionCompany());
        company.setAddress(dto.getAddress());
        company.setType(dto.getType());
        company.setCreatedBy(creator);
        return company;
    }

    public Account findByUsername(String username) {
        return accRepository.findByUsername(username).orElse(null);
    }

    public Account findByEmail(String email) {
        return accRepository.findByEmail(email).orElse(null);
    }

    public Account findByName(String name) {
        return accRepository.findByName(name).orElse(null);
    }

    public List<Account> searchByKeyword(String keyword) {
        return accRepository.searchByKeyword(keyword);
    }

    //Account(USER + MANAGER_ kết thúc
}
