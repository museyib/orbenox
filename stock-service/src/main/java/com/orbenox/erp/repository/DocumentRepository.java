package com.orbenox.erp.repository;

import com.orbenox.erp.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long>
{
}
