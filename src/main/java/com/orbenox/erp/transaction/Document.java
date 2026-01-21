package com.orbenox.erp.transaction;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.common.enums.DocumentStatus;
import com.orbenox.erp.domain.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Document extends BaseEntity {

    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private String description;
}
