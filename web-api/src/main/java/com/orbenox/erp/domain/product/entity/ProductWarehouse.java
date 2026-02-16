package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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
  private BigDecimal quantity = BigDecimal.valueOf(0);
  @Column(nullable = false, precision = 20, scale = 10)
  private BigDecimal reservedQuantity = BigDecimal.valueOf(0);
  @Column(nullable = false, precision = 20, scale = 10)
  @Generated(event = {EventType.INSERT, EventType.UPDATE})
  private BigDecimal freeQuantity;
  @Column(nullable = false, precision = 20, scale = 10)
  private BigDecimal minQuantity = BigDecimal.valueOf(0);
  @Column(nullable = false, precision = 20, scale = 10)
  private BigDecimal maxQuantity = BigDecimal.valueOf(999999999);

    public ProductWarehouse(Product product, Warehouse warehouse) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = BigDecimal.ZERO;
    }

    public ProductWarehouse() {

    }
}