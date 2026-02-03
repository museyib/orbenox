package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

@Getter
@Setter
@Entity
public class JournalLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private JournalEntry journalEntry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private BusinessPartner partner;

    @Column(nullable = false)
    private BigDecimal debit;

    @Column(nullable = false)
    private BigDecimal credit;

    @PrePersist
    @PreUpdate
    private void validate() {
        if ((debit.equals(ZERO) && credit.equals(ZERO))
                || (!debit.equals(ZERO) && !Objects.equals(credit, ZERO)))
            throw new IllegalStateException("Debit and Credit both set");
    }
}