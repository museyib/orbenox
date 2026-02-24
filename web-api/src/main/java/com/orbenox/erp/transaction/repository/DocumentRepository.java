package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("""
            SELECT d.id as id,
                d.documentNo as documentNo,
                d.documentDate as documentDate,
                d.description as description,
                d.documentStatus as documentStatus,
                d.approvalStatus as approvalStatus,
                t.id as typeId,
                t.code as typeCode,
                t.name as typeName
            FROM Document d
            JOIN d.type t
            ORDER BY d.id DESC
            """)
    List<DocumentItem> getAllItems();

    @Query("""
            SELECT d.id as id,
                d.documentNo as documentNo,
                d.documentDate as documentDate,
                d.description as description,
                d.documentStatus as documentStatus,
                d.approvalStatus as approvalStatus,
                t as typeItem,
                ws as sourceWarehouse,
                wt as targetWarehouse,
                p as businessPartner,
                pl as priceList
            FROM Document d
            LEFT JOIN d.type t
            LEFT JOIN d.stockContext.sourceWarehouse ws
            LEFT JOIN d.stockContext.targetWarehouse wt
            LEFT JOIN d.commercialContext.partner p
            LEFT JOIN d.commercialContext.priceList pl
            WHERE d.id = :id
            """)
    DocumentItem getItemById(@Param("id") Long id);
}
