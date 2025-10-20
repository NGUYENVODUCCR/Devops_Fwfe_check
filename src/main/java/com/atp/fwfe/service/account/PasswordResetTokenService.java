package com.atp.fwfe.service.account;

import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.account.PasswordResetToken;
import com.atp.fwfe.repository.account.AccRepository;
import com.atp.fwfe.repository.account.PasswordResetTokenRepository;
import com.atp.fwfe.service.mailer.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final AccRepository accRepository;
    private final MailService mailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> sendResetCode(String email) throws MessagingException {
        Optional<Account> optional = accRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Email không tồn tại hoặc chưa đăng ký tài khoản.");
        }

        Account account = optional.get();

        passwordResetTokenRepository.deleteByAccount(account);

        String code = String.format("%06d", new Random().nextInt(999999));
        PasswordResetToken resetToken = new PasswordResetToken(code, account, Duration.ofMinutes(15));
        passwordResetTokenRepository.save(resetToken);

        String subject = "Mã xác minh đặt lại mật khẩu";
        String content = """
            <div style="font-family:Arial; color:#333;">
              <h2>Mã xác minh của bạn</h2>
              <p>Nhập mã này vào ứng dụng để đặt lại mật khẩu:</p>
              <h1 style="color:#2d8cf0;">%s</h1>
              <p>Mã sẽ hết hạn sau 15 phút.</p>
            </div>
            """.formatted(code);

        mailService.sendHtml(email, subject, content);

        return ResponseEntity.ok("Đã gửi mã xác minh đến email.");
    }

    public ResponseEntity<?> verifyCode(String email, String code) {
        Optional<Account> optional = accRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại.");
        }

        Account account = optional.get();

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(code);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã xác minh không đúng.");
        }

        PasswordResetToken token = tokenOpt.get();

        if (!token.getAccount().getId().equals(account.getId())) {
            return ResponseEntity.badRequest().body("Mã xác minh không khớp với tài khoản.");
        }

        if (token.isExpired()) {
            return ResponseEntity.badRequest().body("Mã xác minh đã hết hạn.");
        }

        return ResponseEntity.ok("Mã hợp lệ.");
    }

    public ResponseEntity<?> resetPassword(String email, String code, String newPassword) {
        Optional<Account> optional = accRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại.");
        }

        Account account = optional.get();

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(code);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã xác minh không đúng.");
        }

        PasswordResetToken token = tokenOpt.get();

        if (!token.getAccount().getId().equals(account.getId())) {
            return ResponseEntity.badRequest().body("Mã không khớp với tài khoản.");
        }

        if (token.isExpired()) {
            return ResponseEntity.badRequest().body("Mã đã hết hạn.");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accRepository.save(account);

        passwordResetTokenRepository.delete(token);

        return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công.");
    }
}
