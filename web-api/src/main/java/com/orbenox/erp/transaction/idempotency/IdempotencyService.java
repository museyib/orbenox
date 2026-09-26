package com.orbenox.erp.transaction.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.*;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private static final long TTL_HOURS = 24;

    private final IdempotencyRepository idempotencyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper jsonMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryLock(String key, String requestHash) {
        try {
            IdempotencyEntity entity = new IdempotencyEntity();
            entity.setIdempotencyKey(key);
            entity.setStatus(PROCESSING.name());
            entity.setRequestHash(requestHash);
            idempotencyRepository.saveAndFlush(entity);

            IdempotentRecord record = new IdempotentRecord();
            record.setStatus(PROCESSING);
            record.setRequestHash(requestHash);

            redisTemplate.opsForValue().set(key, record, Duration.ofHours(TTL_HOURS));

            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public IdempotentRecord getRecord(String key) {
        IdempotentRecord cachedRecord = (IdempotentRecord) redisTemplate.opsForValue().get(key);
        if (cachedRecord != null)
            return cachedRecord;

        return idempotencyRepository.findByIdempotencyKey(key)
                .map(entity -> {
                    IdempotentRecord record = new IdempotentRecord();
                    record.setStatus(IdempotentRecord.Status.valueOf(entity.getStatus()));
                    record.setRequestHash(entity.getRequestHash());
                    record.setResponseStatus(entity.getResponseStatus());
                    record.setResponseBody(entity.getResponseBody());

                    redisTemplate.opsForValue().set(key, record, Duration.ofHours(TTL_HOURS));
                    return record;
                })
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(String key, int responseStatus, String requestHash, Object responseBody) {
        String jsonBody = jsonMapper.writeValueAsString(responseBody);

        idempotencyRepository.findByIdempotencyKey(key)
                .ifPresent(entity -> {
                    entity.setStatus(COMPLETED.name());
                    entity.setResponseStatus(responseStatus);
                    entity.setResponseBody(jsonBody);
                    idempotencyRepository.save(entity);
                });

        IdempotentRecord record = new IdempotentRecord();
        record.setStatus(COMPLETED);
        record.setRequestHash(requestHash);
        record.setResponseStatus(responseStatus);
        record.setResponseBody(jsonBody);

        redisTemplate.opsForValue().set(key, record, Duration.ofHours(TTL_HOURS));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void evict(String key) {
        redisTemplate.delete(key);
        idempotencyRepository.deleteByIdempotencyKey(key);
    }
}
