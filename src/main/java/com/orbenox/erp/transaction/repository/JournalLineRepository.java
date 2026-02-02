package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.JournalLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalLineRepository extends JpaRepository<JournalLine, Long> {
}
