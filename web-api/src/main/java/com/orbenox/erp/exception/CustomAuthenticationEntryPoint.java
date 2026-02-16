package com.orbenox.erp.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.orbenox.erp.common.Utilities.getMessage;
import static com.orbenox.erp.common.Utilities.writeErrorResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), getMessage(exception));
    }
}
