package com.orbenox.erp.unit;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link UnitDimension}
 */
@Data
public final class UnitDimensionDto implements Serializable {
    private Long id;
    private String code;
    private String name;
}