package com.orbenox.erp.outbox;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.status = :status, e.publishedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")})
    List<OutboxEvent> findAllByStatusOrderByCreatedAt(String status, Limit limit);
}
