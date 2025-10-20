package com.atp.fwfe.repository.account;

import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.account.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByAccount(Account account);
}
