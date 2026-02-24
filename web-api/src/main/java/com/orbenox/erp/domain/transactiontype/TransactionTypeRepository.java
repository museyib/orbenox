package com.orbenox.erp.domain.transactiontype;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
    @Query("""
        SELECT t.id AS id,
            t.code AS code,
            t.name AS name,
            t.stockAffectDirection AS stockAffectDirection,
            t.commercialAffected AS commercialAffected,
            t.accountingAffected AS accountingAffected,
            t.creditLimitChecked AS creditLimitChecked,
            t.approvalRequired AS approvalRequired,
            t.enabled AS enabled
        FROM TransactionType t
        WHERE t.deleted = false
        ORDER BY t.id""")
    Slice<TransactionTypeItem> getAllItems(Pageable pageable);

@Query("""
        SELECT t.id AS id,
            t.code AS code,
            t.name AS name,
            t.stockAffectDirection AS stockAffectDirection,
            t.commercialAffected AS commercialAffected,
            t.accountingAffected AS accountingAffected,
            t.creditLimitChecked AS creditLimitChecked,
            t.approvalRequired AS approvalRequired,
            t.enabled AS enabled
        FROM TransactionType t
        WHERE  t.deleted = false
                    AND (LOWER(t.code) LIKE %:search% OR LOWER(t.name) LIKE %:search%)
            ORDER BY t.id""")
    Slice<TransactionTypeItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
        SELECT t.id AS id,
            t.code AS code,
            t.name AS name
        FROM TransactionType t
        WHERE t.deleted = false AND t.enabled = true
        ORDER BY t.id""")
    List<SimpleTransactionTypeItem> getEnabledItems();

    @Query("""
            SELECT t.id as id,
                   t.code as code,
                   t.name as name,
                   t.stockAffectDirection AS stockAffectDirection,
                   t.commercialAffected AS commercialAffected,
                   t.accountingAffected AS accountingAffected,
                   t.creditLimitChecked AS creditLimitChecked,
                   t.approvalRequired AS approvalRequired,
                   t.enabled as enabled,
                   n AS numberingPolicy
            FROM TransactionType t
            LEFT JOIN t.numberingPolicy AS n
            WHERE t.id = :id AND t.deleted = false
            """)
    TransactionTypeItem getItemById(@Param("id") Long id);

    TransactionType findByIdAndDeletedFalse(Long id);

    TransactionType findByCode(String code);
}

