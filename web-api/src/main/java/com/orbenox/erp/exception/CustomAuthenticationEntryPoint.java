package com.orbenox.erp.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
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
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException exception) throws IOException {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), getMessage(exception));
    }
}
