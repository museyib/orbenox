package com.orbenox.erp.transaction.idempotency;

import com.orbenox.erp.exception.BusinessRuleException;
import com.orbenox.erp.exception.IdempotencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.function.ThrowingSupplier;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.COMPLETED;
import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.PROCESSING;

@Service
@RequiredArgsConstructor
public class IdempotencyExecutor {
    private final IdempotencyService idempotencyService;
    private final JsonMapper jsonMapper;

    @Transactional
    public Object execute(String key,
                          String requestHash,
                          ThrowingSupplier<Object> operation) throws Exception {


        boolean locked = idempotencyService.tryLock(key, requestHash);

        if (!locked) {
            IdempotentRecord existingRecord = idempotencyService.getRecord(key);

            if (existingRecord != null) {
                if (!requestHash.equals(existingRecord.getRequestHash())) {
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
            Object result = operation.get();

            ResponseEntity<?> response = (ResponseEntity<?>) result;

            idempotencyService.complete(key, response.getStatusCode().value(), requestHash, response.getBody());

            return result;
        } catch (Exception e) {
            idempotencyService.evict(key);
            throw new Exception(e);
        }
    }
}
