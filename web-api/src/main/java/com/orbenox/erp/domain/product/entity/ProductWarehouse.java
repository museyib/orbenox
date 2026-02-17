package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.warehouse.Warehouse;
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
public class ProductWarehouse extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Warehouse warehouse;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal minQuantity = BigDecimal.valueOf(0);

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal maxQuantity = BigDecimal.valueOf(999999999);
}