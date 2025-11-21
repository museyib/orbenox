package com.orbenox.erp.common.entity;

import com.orbenox.erp.common.enums.DocumentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseTransactionEntity extends BaseEntity {
    @Column(nullable = false)
    private LocalDateTime documentDate;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    private String documentNumber;
}
