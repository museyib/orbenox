package com.orbenox.erp.domain.postingrule;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostingRuleRepository extends JpaRepository<PostingRule, Long> {

    @Query("""
            SELECT pr.id as id,
                   pr.sequence as sequence,
                   pr.type.id as transactionTypeId,
                   pr.type.code as transactionTypeCode,
                   pr.debitAccount.id as debitAccountId,
                   pr.debitAccount.code as debitAccountCode,
                   pr.creditAccount.id as creditAccountId,
                   pr.creditAccount.code as creditAccountCode,
                   pr.amountSource as amountSource,
                   pr.partnerSide as partnerSide
            FROM PostingRule pr
            ORDER BY pr.id""")
    Slice<PostingRuleItem> getAllItems(Pageable pageable);

@Query("""
            SELECT pr.id as id,
                   pr.sequence as sequence,
                   pr.type.id as transactionTypeId,
                   pr.type.code as transactionTypeCode,
                   pr.debitAccount.id as debitAccountId,
                   pr.debitAccount.code as debitAccountCode,
                   pr.creditAccount.id as creditAccountId,
                   pr.creditAccount.code as creditAccountCode,
                   pr.amountSource as amountSource,
                   pr.partnerSide as partnerSide
            FROM PostingRule pr
            WHERE (LOWER(pr.type.code) LIKE %:search% OR LOWER(pr.debitAccount.code) LIKE %:search% OR LOWER(pr.creditAccount.code) LIKE %:search% OR LOWER(str(pr.amountSource)) LIKE %:search% OR LOWER(str(pr.partnerSide)) LIKE %:search% OR str(pr.sequence) LIKE %:search%)
            ORDER BY pr.id""")
    Slice<PostingRuleItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT pr.id as id,
                   pr.sequence as sequence,
                   pr.type.id as transactionTypeId,
                   pr.type.code as transactionTypeCode,
                   pr.debitAccount.id as debitAccountId,
                   pr.debitAccount.code as debitAccountCode,
                   pr.creditAccount.id as creditAccountId,
                   pr.creditAccount.code as creditAccountCode,
                   pr.amountSource as amountSource,
                   pr.partnerSide as partnerSide
            FROM PostingRule pr
            WHERE pr.id = :id
            """)
    PostingRuleItem getItemById(@Param("id") Long id);
}

