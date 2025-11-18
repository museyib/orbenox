package com.orbenox.erp.unit;

import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
@RequiredArgsConstructor
public class UnitConverterService {
    private final LocalizationService i18n;

    public BigDecimal convert(BigDecimal value, UnitDto from, UnitDto to) {
        if (from == to) {
            return value;
        }
        if (!from.unitDimension().equals(to.unitDimension())) {
            throw new IllegalStateException(i18n.msg("error.differentUnitDimension"));
        }

        BigDecimal inBase = value.add(from.offsetToBase()).multiply(from.factorToBase());

        return inBase.divide(to.factorToBase(), MathContext.DECIMAL64).subtract(to.offsetToBase());
    }
}
