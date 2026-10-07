package com.orbenox.erp.security.service;

import com.orbenox.erp.security.projection.UserItem;
import com.orbenox.erp.security.projection.UserTypeItem;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {
    @Test
    void loadUserByUsername_shouldMapTheApplicationUserAndRoleToSpringSecurity() {
        UserService userService = mock(UserService.class);
        UserItem appUser = mock(UserItem.class);
        UserTypeItem userType = mock(UserTypeItem.class);
        when(userService.getByUsername("alice")).thenReturn(appUser);
        when(appUser.getUsername()).thenReturn("alice");
        when(appUser.getPassword()).thenReturn("encoded");
        when(appUser.getUserType()).thenReturn(userType);
        when(userType.getCode()).thenReturn("MANAGER");

        var details = new CustomUserDetailsService(userService).loadUserByUsername("alice");

        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("encoded");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_MANAGER");
    }

    @Test
    void loadUserByUsername_shouldPropagateUnknownUserFailures() {
        UserService userService = mock(UserService.class);
        when(userService.getByUsername("missing")).thenThrow(new UsernameNotFoundException("missing"));

        assertThatThrownBy(() -> new CustomUserDetailsService(userService).loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("missing");
    }
}
