package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
    List<ProductLine> findAllByDocumentId(Long documentId);
}
