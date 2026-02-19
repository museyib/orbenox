package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.enums.ResetPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class NumberingPolicy {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String documentTypeCode;

    @Column(nullable = false)
    private String prefix;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResetPeriod resetPeriod;

    @Column(nullable = false)
    private int sequenceLength;
}