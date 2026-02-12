package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.AmountSource;
import com.orbenox.erp.enums.PartnerSide;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PostingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(optional = false)
    private TransactionType type;

    @ManyToOne(optional = false)
    private Account debitAccount;

    @ManyToOne(optional = false)
    private Account creditAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmountSource amountSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerSide partnerSide;
}