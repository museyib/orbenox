package com.orbenox.erp.eventpublisher.outbox;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.status = :status, e.publishedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    @Transactional
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    List<OutboxEvent> findAllByStatusOrderByCreatedAt(String status, Limit limit);
}
