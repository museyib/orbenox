package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class ProductUnit extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Product product;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Unit unit;
  @Column(nullable = false, precision = 20, scale = 10)
  private BigDecimal factorToBase;
}