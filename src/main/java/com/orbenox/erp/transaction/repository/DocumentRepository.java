package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByNumber(String number);
}
