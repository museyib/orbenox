package com.orbenox.erp.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.price.PriceList;
import com.orbenox.erp.unit.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class ProductPrice extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PriceList priceList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Unit unit;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal factorToParent;

    private Boolean fixedPrice;
}