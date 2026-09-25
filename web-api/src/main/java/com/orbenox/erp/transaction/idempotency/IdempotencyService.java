package com.orbenox.erp.transaction.idempotency;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.orbenox.erp.transaction.idempotency.IdempotentRecord.Status.*;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private static final long TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper jsonMapper;

    public boolean tryLock(String key, String requestHash) {
        IdempotentRecord record = new IdempotentRecord();
        record.setStatus(PROCESSING);
        record.setRequestHash(requestHash);

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, record, Duration.ofHours(TTL_HOURS));

        return Boolean.TRUE.equals(success);
    }

    public IdempotentRecord getRecord(String key) {
        return (IdempotentRecord) redisTemplate.opsForValue().get(key);
    }

    public void complete(String key, int responseStatus, String requestHash, Object responseBody) {
        IdempotentRecord record = new IdempotentRecord();
        record.setStatus(COMPLETED);
        record.setRequestHash(requestHash);
        record.setResponseStatus(responseStatus);
        record.setResponseBody(jsonMapper.writeValueAsString(responseBody));

        redisTemplate.opsForValue().set(key, record, Duration.ofHours(TTL_HOURS));
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }
}
