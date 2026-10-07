package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    JournalEntry findByDocumentId(Long documentId);

    @Modifying
    @Query(value = """
               INSERT INTO journal_entry (document_id, status) VALUES (:documentId, :status)
               ON CONFLICT (document_id) DO NOTHING
            """, nativeQuery = true)
    int save(@Param("documentId") Long documentId,
             @Param("status") String status);
}
