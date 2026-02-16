package com.orbenox.erp.security.service;

import com.orbenox.erp.security.projection.UserItem;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
    private final UserService userService;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserItem userItem = userService.getByUsername(username);
        return new User(
                userItem.getUsername(),
                userItem.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userItem.getUserType().getCode()))
        );
    }
}
