package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProductBarcode extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Unit unit;
    @Column(nullable = false, unique = true)
    private String barcode;
}