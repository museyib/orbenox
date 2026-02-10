package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    JournalEntry findByDocumentId(Long documentId);
}
