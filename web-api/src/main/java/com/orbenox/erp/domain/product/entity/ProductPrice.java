package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.unit.Unit;
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
public class ProductPrice extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PriceList priceList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Unit unit;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal factorToParent;

    private boolean fixedPrice;

    private Short roundLength;

    private BigDecimal discountRatioLimit;
}