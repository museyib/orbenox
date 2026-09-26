package com.orbenox.erp.idempotency;

import com.orbenox.erp.outbox.OutboxEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;
import java.util.UUID;

import static com.orbenox.erp.common.Utilities.isBlank;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final IdempotencyExecutor idempotencyExecutor;
    private final JsonMapper jsonMapper;

    @Around("@annotation(idempotentConfig)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotentConfig) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();

        String key = request.getHeader(idempotentConfig.headerName());

        if (isBlank(key)) {
            return joinPoint.proceed();
        }

        Object bodyArg = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Fingerprintable)
                .findFirst()
                .orElse(null);

        String currentRequestHash = "";

        if (bodyArg instanceof Fingerprintable fingerprintable) {
            currentRequestHash = DigestUtils.sha256Hex(jsonMapper.writeValueAsString(fingerprintable));
            key = fingerprintable.tag() + ":" + key;
        }

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(idempotentConfig.eventType());
        outboxEvent.setAggregateType(idempotentConfig.aggregateType());
        outboxEvent.setAggregateId(UUID.randomUUID().toString());
        outboxEvent.setPayload(jsonMapper.writeValueAsString(bodyArg));
        outboxEvent.setStatus("PENDING");

        return idempotencyExecutor.execute(key, currentRequestHash, outboxEvent, () -> {
            try {
                return joinPoint.proceed();
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
