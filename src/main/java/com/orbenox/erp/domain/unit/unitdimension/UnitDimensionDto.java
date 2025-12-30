package com.orbenox.erp.domain.unit.unitdimension;

import java.io.Serializable;

/**
 * DTO for {@link UnitDimension}
 */
public record UnitDimensionDto(Long id, Boolean enabled, String code, String name) implements Serializable {
}