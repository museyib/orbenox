package com.orbenox.erp.product.entity;

import com.orbenox.erp.unit.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product_price")
public class ProductPrice {
    @EmbeddedId
    private ProductPriceId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("priceId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "price_id", nullable = false)
    private Price price;

    @MapsId("unitId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "price", nullable = false)
    private BigDecimal priceValue;

    private BigDecimal factorToBase;

    private Boolean fixedPrice;


}