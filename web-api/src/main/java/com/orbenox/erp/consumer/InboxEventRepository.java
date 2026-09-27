package com.orbenox.erp.consumer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO inbox_event(consumer_name, event_id, processed_at)
            VALUES (:consumerName, :eventId, CURRENT_TIMESTAMP)
            ON CONFLICT DO NOTHING""",
            nativeQuery = true)
    void createInboxEvent(@Param("consumerName") String consumerName,
                          @Param("eventId") Long eventId);
}
