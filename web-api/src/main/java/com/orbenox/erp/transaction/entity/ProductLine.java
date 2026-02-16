package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.unit.Unit;
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
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @Column(nullable = false)
    private BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Unit unit;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal discount;

    @Override
    public String toString() {
        return "ProductLine{" +
                "id=" + id +
                ", document=" + document +
                ", product=" + product +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", unitPrice=" + unitPrice +
                ", discount=" + discount +
                '}';
    }
}