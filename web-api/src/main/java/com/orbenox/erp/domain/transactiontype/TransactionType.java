package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.postingrule.PostingRule;
import com.orbenox.erp.enums.StockAffectDirection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class TransactionType extends BaseCardEntity {
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String documentNoPrefix;

    @Enumerated(EnumType.STRING)
    private StockAffectDirection stockAffectDirection;

    @Column(nullable = false)
    private boolean commercialAffected;

    @Column(nullable = false)
    private boolean accountingAffected;

    @Column(nullable = false)
    private boolean creditLimitChecked;

    @Column(nullable = false)
    private boolean approvalRequired;

    @OneToMany(mappedBy = "type")
    private Set<PostingRule> rules;

    public boolean isStockAffected() {
        return stockAffectDirection != null;
    }
}