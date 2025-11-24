package com.orbenox.erp.product.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.currency.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
public class Price extends BaseCardEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Currency currency;

    @ManyToOne
    private Price parent;

    private BigDecimal factorToParent;

    @ManyToMany
    @JoinTable(
            name = "product_price",
            joinColumns = @JoinColumn(name = "price_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductPrice> products;
}