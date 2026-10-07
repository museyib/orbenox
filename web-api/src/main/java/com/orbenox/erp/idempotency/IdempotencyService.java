package com.orbenox.erp.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import static com.orbenox.erp.idempotency.IdempotencyRecord.Status.COMPLETED;
import static com.orbenox.erp.idempotency.IdempotencyRecord.Status.PROCESSING;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final IdempotencyRepository idempotencyRepository;
    private final JsonMapper jsonMapper;

    @Transactional
    public boolean tryLock(String key, String requestHash) {

        int affected = idempotencyRepository.createIdempotency(key, PROCESSING.name(), requestHash);

        return affected > 0;
    }

    public IdempotencyRecord getRecord(String key) {
        return idempotencyRepository.findByIdempotencyKey(key).orElse(null);
    }

    @Transactional
    public void complete(String key, int responseStatus, String requestHash, Object responseBody) {
        String jsonBody = jsonMapper.writeValueAsString(responseBody);

        idempotencyRepository.findByIdempotencyKey(key)
                .ifPresent(entity -> {
                    entity.setStatus(COMPLETED);
                    entity.setRequestHash(requestHash);
                    entity.setResponseStatus(responseStatus);
                    entity.setResponseBody(jsonBody);
                    idempotencyRepository.save(entity);
                });
    }
}
