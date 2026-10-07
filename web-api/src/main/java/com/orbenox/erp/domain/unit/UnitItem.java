package com.orbenox.erp.domain.unit;

import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionItem;

import java.math.BigDecimal;

public interface UnitItem {
    Long getId();

    String getCode();

    String getName();

    boolean isEnabled();

    boolean isBase();

    BigDecimal getFactorToBase();

    BigDecimal getOffsetToBase();

    UnitDimensionItem getUnitDimension();
}
