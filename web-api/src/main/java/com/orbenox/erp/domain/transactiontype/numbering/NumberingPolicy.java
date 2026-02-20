package com.orbenox.erp.domain.transactiontype.numbering;

import com.orbenox.erp.domain.transactiontype.TransactionType;
import com.orbenox.erp.enums.ResetPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class NumberingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id")
    private TransactionType type;

    @Column(nullable = false)
    private String prefix;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResetPeriod resetPeriod;

    @Column(nullable = false)
    private int sequenceLength;
}