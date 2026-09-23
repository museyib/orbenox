package com.orbenox.erp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.json.JsonMapper;

import static com.orbenox.erp.IdempotentRecord.Status.COMPLETED;
import static com.orbenox.erp.IdempotentRecord.Status.PROCESSING;

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyService idempotencyService;

    private final JsonMapper jsonMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod()) && !"PATCH".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String key = request.getHeader("Idempotency-Key");

        if (key == null || key.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing Idempotency-Key header");
            return false;
        }

        IdempotentRecord record = idempotencyService.getRecord(key);

        if (record != null) {
            if (PROCESSING.equals(record.getStatus())) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Request is already being processed");
                return false;
            } else if (COMPLETED.equals(record.getStatus())) {
                response.setStatus(record.getResponseStatus());
                response.setContentType("application/json");
                response.getWriter().write(jsonMapper.writeValueAsString(record.getResponseBody()));
                return false;
            }
        }

        if (!idempotencyService.tryLock(key)) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Concurrent request execution detected");
            return false;
        }

        request.setAttribute("currentIdempotencyKey", key);
        return true;
    }
}
