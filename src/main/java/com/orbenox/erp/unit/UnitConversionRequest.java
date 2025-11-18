package com.orbenox.erp.unit;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnitConversionRequest {
    private UnitDto from;
    private UnitDto to;
    private BigDecimal value;
}
