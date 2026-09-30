package com.orbenox.erp.stockservice.repository;

import com.orbenox.erp.stockservice.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long>
{
}
