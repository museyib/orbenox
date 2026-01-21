package com.orbenox.erp.domain.transaction;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TransactionType extends BaseCardEntity {
  @Column(nullable = false, unique = true)
  private String code;
  @Column(nullable = false)
  private String name;

  private boolean affectsStock;
  private boolean affectsAR;
  private boolean creditLimitCheck;
}