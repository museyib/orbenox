package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.account.Account;
import com.orbenox.erp.domain.businesspartner.BusinessPartner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private BusinessPartner partner;

    private BigDecimal debit;
    private BigDecimal credit;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (debit.signum() > 0 && credit.signum() > 0)
            throw new IllegalStateException("Debit and Credit both set");
    }
}