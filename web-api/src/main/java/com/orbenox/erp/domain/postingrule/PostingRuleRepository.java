package com.orbenox.erp.domain.postingrule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    List<PostingRuleItem> getAllItems();

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
