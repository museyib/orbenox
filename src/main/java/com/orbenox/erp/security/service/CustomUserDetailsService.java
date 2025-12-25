package com.orbenox.erp.security.service;

import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserItem appUser = userRepository.getItemByUsername(username);
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getUserType().getCode()))
        );
    }
}
