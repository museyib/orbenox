package com.orbenox.erp.stockservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class ProductLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Document document;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_id", nullable = false)
    private Long unit;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal discount;

    @Override
    public String toString() {
        return "ProductLine{" +
                "id=" + id +
                ", document=" + document +
                ", product=" + productId +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", unitPrice=" + unitPrice +
                ", discount=" + discount +
                '}';
    }
}
