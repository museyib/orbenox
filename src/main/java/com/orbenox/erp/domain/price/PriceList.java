package com.orbenox.erp.domain.price;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.currency.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class PriceList extends BaseCardEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PriceList parent;

    @Column(nullable = false)
    private BigDecimal factorToParent;

    @Column(nullable = false)
    private Short roundLength;
}