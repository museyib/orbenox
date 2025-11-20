package com.orbenox.erp.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.currency.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Price extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Currency currency;

    @ManyToOne
    private Price parent;

    private BigDecimal factorToParent;

    private Boolean enabled;

    private Boolean deleted;
}