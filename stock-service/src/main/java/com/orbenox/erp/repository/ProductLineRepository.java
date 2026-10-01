package com.orbenox.erp.repository;

import com.orbenox.erp.entity.ProductLine;
import com.orbenox.erp.projection.ProductLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {

    @Query("""
        SELECT l.id AS id,
            l.productId AS productId,
            l.quantity AS quantity,
            l.unitPrice AS unitPrice,
            l.discount AS discount,
            s.sourceWarehouseId AS sourceWarehouseId,
            s.targetWarehouseId AS targetWarehouseId
        FROM ProductLine l
        LEFT JOIN l.document d
        LEFT JOIN d.stockContext s
        WHERE l.document.id = :id
        """)
    List<ProductLineItem> getItemsByDocumentId(@Param("id") Long id);
}
