package com.orbenox.erp.transaction.idempotency;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.exception.IdempotencyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;

import static com.orbenox.erp.common.Utilities.isBlank;
import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.COMPLETED;
import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.PROCESSING;
import static org.springframework.http.HttpStatus.OK;

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

        Object bodyArg = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Fingerprintable)
                .findFirst()
                .orElse(null);

        String currentRequestHash = "";

        if (bodyArg instanceof Fingerprintable fingerprintable) {
            currentRequestHash = DigestUtils.sha256Hex(jsonMapper.writeValueAsString(fingerprintable));
            key = fingerprintable.tag() + ":" + key;
        }


        boolean locked = idempotencyService.tryLock(key, currentRequestHash);

        if (!locked) {
            IdempotentRecord existingRecord = idempotencyService.getRecord(key);

            if (existingRecord != null) {
                if (!currentRequestHash.equals(existingRecord.getRequestHash())) {
                    throw new IdempotencyException("Idempotency Key conflict: This key was already used for a different payload.");
                }

                if (PROCESSING.equals(existingRecord.getStatus())) {
                    throw new BusinessRuleException("Request is already being processed.");
                } else if (COMPLETED.equals(existingRecord.getStatus())) {
                    JsonNode body = jsonMapper.readTree(existingRecord.getResponseBody());
                    return ResponseEntity.status(existingRecord.getResponseStatus())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(body);
                }
            }

            throw new IdempotencyException("Concurrent request processing error.");
        }

        try {
            Object result = joinPoint.proceed();

            ResponseEntity<?> response = (ResponseEntity<?>) result;

            idempotencyService.complete(key, response.getStatusCode().value(), currentRequestHash, response.getBody());

            return result;
        } catch (Exception e) {
            idempotencyService.evict(key);
            throw e;
        }
    }
}
