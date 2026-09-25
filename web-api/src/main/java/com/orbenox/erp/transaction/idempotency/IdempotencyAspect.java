package com.orbenox.erp.transaction.idempotency;

import com.orbenox.erp.exception.BusinessRuleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;

import static com.orbenox.erp.common.Utilities.isBlank;
import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.COMPLETED;
import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.PROCESSING;
import static org.springframework.http.HttpStatus.CONFLICT;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final IdempotencyService idempotencyService;
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

        String currentRequestHash = "";
        Object bodyArg = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Fingerprintable)
                .findFirst()
                .orElse(null);

        if (bodyArg instanceof Fingerprintable fingerprintable) {
            currentRequestHash = DigestUtils.sha256Hex(fingerprintable.getFingerprintFields().toString());
        }

        boolean locked = idempotencyService.tryLock(key, currentRequestHash);

        if (!locked) {
            IdempotentRecord existingRecord = idempotencyService.getRecord(key);

            if (existingRecord != null) {
                if (!currentRequestHash.equals(existingRecord.getRequestHash())) {
                    throw new ResponseStatusException(CONFLICT, "Idempotency Key conflict: This key was already used for a different payload.");
                }

                if (PROCESSING.equals(existingRecord.getStatus())) {
                    throw new BusinessRuleException("Request is already being processed or has completed");
                } else if (COMPLETED.equals(existingRecord.getStatus())) {
                    Class<?> returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
                    return jsonMapper.readValue((String) existingRecord.getResponseBody(), returnType);
                }
            }

            throw new ResponseStatusException(CONFLICT, "Concurrent request processing error.");
        }

        try {
            Object result = joinPoint.proceed();

            idempotencyService.complete(key, currentRequestHash, result);

            return result;
        } catch (Exception e) {
            idempotencyService.evict(key);
            throw e;
        }
    }
}
