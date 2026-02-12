package com.orbenox.erp.domain.unit;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.unit.unitdimension.UnitDimension;
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
public class Unit extends BaseCardEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UnitDimension unitDimension;

    @Column(nullable = false)
    private boolean base;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal factorToBase;

    @Column(nullable = false, precision = 20, scale = 10)
    private BigDecimal offsetToBase;
}