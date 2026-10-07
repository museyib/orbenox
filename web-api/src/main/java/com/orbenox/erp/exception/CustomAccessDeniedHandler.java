package com.orbenox.erp.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.orbenox.erp.common.Utilities.getMessage;
import static com.orbenox.erp.common.Utilities.writeErrorResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException exception) throws IOException {
        writeErrorResponse(response, HttpStatus.FORBIDDEN.value(), getMessage(exception));
    }
}
