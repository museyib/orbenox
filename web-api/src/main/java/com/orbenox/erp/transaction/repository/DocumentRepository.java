package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByNumber(String number);

    @Query("""
            SELECT d.id as id,
                d.documentNumber as number,
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
                d.documentNumber as number,
                d.documentDate as documentDate,
                d.description as description,
                d.documentStatus as documentStatus,
                d.approvalStatus as approvalStatus,
                t.id as typeId,
                t.code as typeCode,
                t.name as typeName
            FROM Document d
            JOIN d.type t
            WHERE d.id = :id
            """)
    DocumentItem getItemById(@Param("id") Long id);
}
