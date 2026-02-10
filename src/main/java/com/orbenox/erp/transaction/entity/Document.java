package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.ApprovalStatus;
import com.orbenox.erp.enums.DocumentStatus;
import com.orbenox.erp.security.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Document extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private LocalDate documentDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private DocumentStatus documentStatus;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser owner;

    private String description;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private CommercialContext commercialContext;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private StockContext stockContext;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private JournalEntry journalEntry;

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<ProductLine> productLines = new ArrayList<>();

    public boolean isPosted() {
        return documentStatus == DocumentStatus.POSTED;
    }
}
