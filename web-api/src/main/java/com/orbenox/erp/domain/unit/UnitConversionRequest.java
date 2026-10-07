package com.orbenox.erp.domain.unit;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnitConversionRequest {
    private UnitUpdateDto from;
    private UnitUpdateDto to;
    private BigDecimal value;
}
