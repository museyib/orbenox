package com.orbenox.erp.common.entity;

import com.orbenox.erp.common.enums.DocumentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseTransactionEntity {
    @Column(nullable = false)
    private LocalDateTime documentDate;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    private String documentNumber;
}
