package com.orbenox.erp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Value("${spring.application.name:web-api}")
    private String serviceName;

    //Correlation ID pattern: ^[a-zA-Z0-9_-]{1,100}$
    private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{1,100}$");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String incomingCorrelationId = request.getHeader(CORRELATION_ID_HEADER);
        if (incomingCorrelationId == null) {
            incomingCorrelationId = serviceName + "_" + UUID.randomUUID();
        } else {
            Matcher matcher = pattern.matcher(incomingCorrelationId);
            if (!matcher.matches()) {
                MDC.remove(CORRELATION_ID_HEADER);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid correlation ID");
                return;
            }
        }
        MDC.put(CORRELATION_ID_HEADER, incomingCorrelationId);
        response.setHeader(CORRELATION_ID_HEADER, incomingCorrelationId);
        request.setAttribute(CORRELATION_ID_HEADER, incomingCorrelationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_HEADER);
        }
    }
}
