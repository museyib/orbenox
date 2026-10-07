package com.orbenox.erp.security.config;

import com.orbenox.erp.localization.LocalizationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {
    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;
    private LocalizationService localizationService;
    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtUtil = mock(JwtUtil.class);
        userDetailsService = mock(UserDetailsService.class);
        localizationService = mock(LocalizationService.class);
        when(localizationService.msg(anyString(), any())).thenReturn("localized");
        filter = new JwtAuthFilter(jwtUtil, userDetailsService, localizationService);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldAuthenticateValidBearerTokenAndContinueChain() throws ServletException, IOException {
        UserDetails user = User.withUsername("alice").password("secret").roles("USER").build();
        when(jwtUtil.extractUsername("valid")).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(user);
        when(jwtUtil.isTokenValid("valid", user)).thenReturn(true);
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(newRequest("Bearer valid"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("alice");
        assertThat(chain.getRequest()).isNotNull();
        verify(userDetailsService).loadUserByUsername("alice");
    }

    @Test
    void doFilterInternal_shouldIgnoreMissingOrNonBearerHeaders() throws ServletException, IOException {
        filter.doFilter(newRequest(null), new MockHttpServletResponse(), new MockFilterChain());
        filter.doFilter(newRequest("Basic abc"), new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    void doFilterInternal_shouldLeaveContextUnauthenticatedForMalformedTokensAndContinueChain()
            throws ServletException, IOException {
        when(jwtUtil.extractUsername("bad")).thenThrow(new MalformedJwtException("malformed"));
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(newRequest("Bearer bad"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(chain.getRequest()).isNotNull();
        verify(localizationService).msg("warn.jwt.malformed", "/api/test");
    }

    @Test
    void doFilterInternal_shouldNotReplaceExistingAuthentication() throws ServletException, IOException {
        var original = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "existing-user", null);
        SecurityContextHolder.getContext().setAuthentication(original);
        when(jwtUtil.extractUsername("valid")).thenReturn("alice");

        filter.doFilter(newRequest("Bearer valid"), new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(original);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_shouldLogJwtFailuresAndContinueForInvalidTokens() throws ServletException, IOException {
        when(jwtUtil.extractUsername("expired")).thenThrow(new ExpiredJwtException(null, null, "expired"));
        when(jwtUtil.extractUsername("signature")).thenThrow(new SignatureException("bad signature"));
        when(jwtUtil.extractUsername("unexpected")).thenThrow(new IllegalStateException("unexpected"));
        when(jwtUtil.extractUsername("invalid")).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice"))
                .thenReturn(User.withUsername("alice").password("secret").roles("USER").build());
        when(jwtUtil.isTokenValid(eq("invalid"), any())).thenReturn(false);

        filter.doFilter(newRequest("Bearer expired"), new MockHttpServletResponse(), new MockFilterChain());
        filter.doFilter(newRequest("Bearer signature"), new MockHttpServletResponse(), new MockFilterChain());
        filter.doFilter(newRequest("Bearer unexpected"), new MockHttpServletResponse(), new MockFilterChain());
        filter.doFilter(newRequest("Bearer invalid"), new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(localizationService).msg("warn.jwt.expired", "/api/test");
        verify(localizationService).msg("warn.jwt.invalidSignature", "/api/test");
        verify(localizationService).msg("error.jwt.authentication", "/api/test");
    }

    private MockHttpServletRequest newRequest(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        return request;
    }
}
