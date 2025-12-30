package com.orbenox.erp.domain.unit;

import com.orbenox.erp.domain.unit.unitdimension.UnitDimensionDto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link Unit}
 */
public record UnitDto(Long id, Boolean enabled, String code, String name, UnitDimensionDto unitDimension,
                      Boolean base, BigDecimal factorToBase, BigDecimal offsetToBase) implements Serializable {
}