package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.common.enums.DocumentStatus;
import com.orbenox.erp.domain.transactiontype.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Document {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TransactionType type;

    private String description;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
}
