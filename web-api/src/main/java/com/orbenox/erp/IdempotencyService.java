package com.orbenox.erp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.orbenox.erp.IdempotentRecord.Status.COMPLETED;
import static com.orbenox.erp.IdempotentRecord.Status.PROCESSING;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private static final long TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean tryLock(String key) {
        IdempotentRecord record = new IdempotentRecord();
        record.setStatus(PROCESSING);

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, record, Duration.ofHours(TTL_HOURS));

        return Boolean.TRUE.equals(success);
    }

    public IdempotentRecord getRecord(String key) {
        return (IdempotentRecord) redisTemplate.opsForValue().get(key);
    }

    public void complete(String key, Object responseBody, int status) {
        IdempotentRecord record = new IdempotentRecord();
        record.setStatus(COMPLETED);
        record.setResponseBody(responseBody);
        record.setResponseStatus(status);

        redisTemplate.opsForValue().set(key, record, Duration.ofHours(TTL_HOURS));
    }
}
