package com.orbenox.erp.stockservice.entity;

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

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal reservedQuantity = BigDecimal.valueOf(0);

    @Column(nullable = false, precision = 20, scale = 10)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private BigDecimal freeQuantity;
}
