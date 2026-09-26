package com.orbenox.erp.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;

import static com.orbenox.erp.idempotency.IdempotentRecord.Status.*;

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

    public IdempotentRecord getRecord(String key) {

        return idempotencyRepository.findByIdempotencyKey(key)
                .map(entity -> {
                    IdempotentRecord record = new IdempotentRecord();
                    record.setStatus(IdempotentRecord.Status.valueOf(entity.getStatus()));
                    record.setRequestHash(entity.getRequestHash());
                    record.setResponseStatus(entity.getResponseStatus());
                    record.setResponseBody(entity.getResponseBody());

                    return record;
                })
                .orElse(null);
    }

    @Transactional
    public void complete(String key, int responseStatus, String requestHash, Object responseBody) {
        String jsonBody = jsonMapper.writeValueAsString(responseBody);

        idempotencyRepository.findByIdempotencyKey(key)
                .ifPresent(entity -> {
                    entity.setStatus(COMPLETED.name());
                    entity.setRequestHash(requestHash);
                    entity.setResponseStatus(responseStatus);
                    entity.setResponseBody(jsonBody);
                    idempotencyRepository.save(entity);
                });
    }
}
