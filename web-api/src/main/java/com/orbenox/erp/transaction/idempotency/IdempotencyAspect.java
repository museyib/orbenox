package com.orbenox.erp.transaction.idempotency;

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

        return idempotencyExecutor.execute(key, currentRequestHash, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
