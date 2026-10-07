package com.orbenox.erp.security.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class JwtUtilTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        setField(jwtUtil, "SECRET_KEY", SECRET);
        setField(jwtUtil, "EXPIRATION_TIME", 60_000L);
    }

    @Test
    void generateToken_shouldContainUsernameAndValidateAgainstUserDetails() {
        var user = User.withUsername("alice").password("secret").roles("ADMIN", "USER").build();

        String token = jwtUtil.generateToken(user);

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtUtil.isTokenExpired(token)).isFalse();
        assertThat(jwtUtil.isTokenValid(token, user)).isTrue();
        assertThat(jwtUtil.isTokenValid(token,
                User.withUsername("bob").password("secret").roles("ADMIN").build())).isFalse();
    }

    @Test
    void extractUsername_shouldRejectExpiredAndInvalidlySignedTokens() {
        setField(jwtUtil, "EXPIRATION_TIME", -1L);
        String expiredToken = jwtUtil.generateToken(
                User.withUsername("alice").password("secret").roles("USER").build());

        assertThatThrownBy(() -> jwtUtil.extractUsername(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);

        setField(jwtUtil, "EXPIRATION_TIME", 60_000L);
        String token = jwtUtil.generateToken(
                User.withUsername("alice").password("secret").roles("USER").build());
        JwtUtil otherKey = new JwtUtil();
        setField(otherKey, "SECRET_KEY", "abcdef0123456789abcdef0123456789");

        assertThatThrownBy(() -> otherKey.extractUsername(token))
                .isInstanceOf(SignatureException.class);
    }
}
