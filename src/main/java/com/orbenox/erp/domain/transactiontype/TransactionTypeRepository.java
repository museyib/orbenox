package com.orbenox.erp.domain.transactiontype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    @Query("""
        SELECT t.id AS id,
            t.code AS code,
            t.name AS name,
            t.enabled AS enabled
        FROM TransactionType t
        WHERE t.deleted = false
        ORDER BY t.id""")
    List<TransactionTypeItem> getAllItems();

    @Query("""
        SELECT t.id AS id,
            t.code AS code,
            t.name AS name,
            t.enabled AS enabled
        FROM TransactionType t
        WHERE t.deleted = false AND t.enabled = true
        ORDER BY t.id""")
    List<TransactionTypeItem> getEnabledItems();

    @Query("""
            SELECT t.id as id,
                   t.code as code,
                   t.name as name,
                   t.enabled as enabled
            FROM TransactionType t
            WHERE t.id = :id AND t.deleted = false
            """)
    TransactionTypeItem getItemById(@Param("id") Long id);

    TransactionType findByIdAndDeletedFalse(Long id);

    TransactionType findByCode(String code);
}
