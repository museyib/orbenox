package com.orbenox.erp.transaction.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity, Long> {
    Optional<IdempotencyEntity> findByIdempotencyKey(String idempotencyKey);

    void deleteByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query(value = """
            INSERT INTO idempotency_record(idempotency_key, status, request_hash)
            VALUES (:key, :status, :requestHash)
            ON CONFLICT DO NOTHING""",
            nativeQuery = true)
    int createIdempotency(@Param("key") String key,
                           @Param("status") String status,
                           @Param("requestHash") String requestHash);
}
