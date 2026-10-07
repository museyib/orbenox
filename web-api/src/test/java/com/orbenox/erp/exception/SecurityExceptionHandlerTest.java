package com.orbenox.erp.exception;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityExceptionHandlerTest {
    @Test
    void authenticationEntryPoint_shouldWriteUnauthorizedJsonResponse() throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new CustomAuthenticationEntryPoint().commence(new MockHttpServletRequest(), response,
                new BadCredentialsException("invalid password"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentAsString()).contains("\"code\":401", "invalid password");
    }

    @Test
    void accessDeniedHandler_shouldWriteForbiddenJsonResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new CustomAccessDeniedHandler().handle(new MockHttpServletRequest(), response,
                new AccessDeniedException("permission required"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains("\"code\":403", "permission required");
    }
}
