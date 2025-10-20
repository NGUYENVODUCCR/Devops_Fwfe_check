package com.atp.fwfe.service.account;

import com.atp.fwfe.model.account.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isLocked;

    public CustomUserDetails(Account account, Collection<? extends GrantedAuthority> authorities) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.authorities = authorities;
        this.isLocked = account.isLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isLocked;
    }
}
