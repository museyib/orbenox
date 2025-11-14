package com.orbenox.erp.unit;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link Unit}
 */
public record UnitDto(Long id, String code, String name, UnitDimensionDto unitDimension,
                      boolean isBase, BigDecimal factorToBase, BigDecimal offsetToBase)
        implements Serializable {
}