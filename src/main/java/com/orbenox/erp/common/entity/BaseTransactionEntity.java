package com.orbenox.erp.common.entity;

import com.orbenox.erp.common.enums.DocumentStatus;
import com.orbenox.erp.domain.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseTransactionEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private String transactionNumber;
}
