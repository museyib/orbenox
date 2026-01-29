package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Document extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TransactionType type;

    private String description;

    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @OneToOne(mappedBy = "document")
    private CommercialContext commercialContext;

    @OneToOne(mappedBy = "document")
    private ResponsibilityContext responsibilityContext;
}
