package com.orbenox.erp.transaction.idempotency;

import com.orbenox.erp.exception.BusinessRuleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

import static com.orbenox.erp.common.Utilities.isBlank;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final IdempotencyService idempotencyService;

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

        String requestHash = "";
        Object bodyArg = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Fingerprintable)
                .findFirst()
                .orElse(null);

        if (bodyArg instanceof Fingerprintable fingerprintable) {
            requestHash = DigestUtils.sha256Hex(fingerprintable.getFingerprintFields().toString());
        }

        boolean locked = idempotencyService.tryLock(key, requestHash);

        if (!locked) {
            throw new BusinessRuleException("Request is already being processed or has completed");
        }

        try {
            Object result = joinPoint.proceed();

            idempotencyService.complete(key, result);

            return result;
        } catch (Exception e) {
            idempotencyService.fail(key);
            throw e;
        }
    }
}
