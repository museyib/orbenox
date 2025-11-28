package com.orbenox.erp.unit;

import java.io.Serializable;

/**
 * DTO for {@link UnitDimension}
 */
public record UnitDimensionDto(Long id, Boolean enabled, String code, String name) implements Serializable {
}