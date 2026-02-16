package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class StockBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Override
    public String toString() {
        return "StockBalance{" +
                "id=" + id +
                ", product=" + product +
                ", warehouse=" + warehouse +
                ", quantity=" + quantity +
                '}';
    }
}