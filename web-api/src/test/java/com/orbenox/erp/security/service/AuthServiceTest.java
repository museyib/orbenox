package com.orbenox.erp.security.service;

import com.orbenox.erp.security.config.JwtUtil;
import com.orbenox.erp.security.request.LoginRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getToken_shouldAuthenticateCredentialsAndGenerateJwtForPrincipal() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        var principal = User.withUsername("alice").password("encoded").roles("USER").build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(principal)).thenReturn("signed-token");
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("plain");

        assertThat(new AuthService(authenticationManager, jwtUtil).getToken(request)).isEqualTo("signed-token");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);

        var captor = org.mockito.ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getPrincipal()).isEqualTo("alice");
        assertThat(captor.getValue().getCredentials()).isEqualTo("plain");
        verify(jwtUtil).generateToken(principal);
    }

    @Test
    void getToken_shouldReturnNullWhenAuthenticationHasNoPrincipal() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("plain");

        assertThat(new AuthService(authenticationManager, jwtUtil).getToken(request)).isNull();
        verifyNoInteractions(jwtUtil);
    }
}
