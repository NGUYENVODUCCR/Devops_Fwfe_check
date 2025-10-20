package com.atp.fwfe.service.account;

import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.repository.account.AccRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccRepository accRepository;

    public CustomUserDetailsService (AccRepository accRepository) {
        this.accRepository = accRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User không tìm thấy trong db"));

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(account.getRole()));

        return new CustomUserDetails(account, authorities);
    }
}
