package com.orbenox.erp.transaction.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity, Long> {
    Optional<IdempotencyEntity> findByIdempotencyKey(String idempotencyKey);

    void deleteByIdempotencyKey(String idempotencyKey);
}
