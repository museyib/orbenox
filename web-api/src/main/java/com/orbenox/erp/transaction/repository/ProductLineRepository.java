package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.ProductLine;
import com.orbenox.erp.transaction.projection.ProductLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
    List<ProductLine> findAllByDocumentId(Long documentId);

    @Query("""
        SELECT l.id AS id,
            p AS product,
            l.quantity AS quantity,
            l.unitPrice AS unitPrice,
            l.discount AS discount
        FROM ProductLine l
        LEFT JOIN l.product p
        WHERE l.document.id = :id
        """)
    List<ProductLineItem> getItemsByDocumentId(@Param("id") Long id);
}
