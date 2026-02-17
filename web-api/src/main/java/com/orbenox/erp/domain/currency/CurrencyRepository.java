package com.orbenox.erp.domain.currency;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    @Query("""
            SELECT c.id as id,
                   c.code as code,
                   c.name as name,
                   c.enabled as enabled
            FROM Currency c
            WHERE c.deleted = false
            ORDER BY c.id""")
    Slice<CurrencyItem> getAllItems(Pageable pageable);

@Query("""
            SELECT c.id as id,
                   c.code as code,
                   c.name as name,
                   c.enabled as enabled
            FROM Currency c
            WHERE  c.deleted = false
                    AND (LOWER(c.code) LIKE %:search% OR LOWER(c.name) LIKE %:search%)
            ORDER BY c.id""")
    Slice<CurrencyItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT c.id as id,
                   c.code as code,
                   c.name as name,
                   c.enabled as enabled
            FROM Currency c
            WHERE c.deleted = false AND c.enabled = true
            ORDER BY c.id""")
    List<CurrencyItem> getEnabledItems();

    @Query("""
            SELECT c.id as id,
                   c.code as code,
                   c.name as name,
                   c.enabled as enabled
            FROM Currency c
            WHERE c.id = :id AND c.deleted = false
            """)
    CurrencyItem getItemById(@Param("id") Long id);

    Currency findByIdAndDeletedFalse(Long id);
}

