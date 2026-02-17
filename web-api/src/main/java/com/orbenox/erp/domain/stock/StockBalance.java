package com.orbenox.erp.domain.stock;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal reservedQuantity = BigDecimal.valueOf(0);

    @Column(nullable = false, precision = 20, scale = 10)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private BigDecimal freeQuantity;
}