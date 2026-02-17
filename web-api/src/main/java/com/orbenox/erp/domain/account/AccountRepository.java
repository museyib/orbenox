package com.orbenox.erp.domain.account;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("""
            SELECT a.id as id,
                   a.code as code,
                   a.name as name,
                   a.accountType as accountType,
                   a.enabled as enabled
            FROM Account a
            WHERE a.deleted = false
            ORDER BY a.id""")
    Slice<AccountItem> getAllItems(Pageable pageable);

@Query("""
            SELECT a.id as id,
                   a.code as code,
                   a.name as name,
                   a.accountType as accountType,
                   a.enabled as enabled
            FROM Account a
            WHERE  a.deleted = false
                    AND (LOWER(a.code) LIKE %:search% OR LOWER(a.name) LIKE %:search%)
            ORDER BY a.id""")
    Slice<AccountItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT a.id as id,
                   a.code as code,
                   a.name as name,
                   a.accountType as accountType,
                   a.enabled as enabled
            FROM Account a
            WHERE a.deleted = false AND a.enabled = true
            ORDER BY a.id""")
    List<AccountItem> getEnabledItems();

    @Query("""
            SELECT a.id as id,
                   a.code as code,
                   a.name as name,
                   a.accountType as accountType,
                   a.enabled as enabled
            FROM Account a
            WHERE a.id = :id AND a.deleted = false
            """)
    AccountItem getItemById(@Param("id") Long id);

    Account findByIdAndDeletedFalse(Long id);
}

