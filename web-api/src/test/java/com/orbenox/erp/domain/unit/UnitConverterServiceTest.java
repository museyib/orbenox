package com.orbenox.erp.domain.unit;

import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnitConverterServiceTest {

    private final LocalizationService localizationService = mock(LocalizationService.class);
    private final UnitConverterService converter = new UnitConverterService(localizationService);

    @Test
    void convert_whenSameUnitInstance_shouldReturnOriginalValue() {
        UnitUpdateDto unit = unit(1L, "1", "0");
        BigDecimal value = BigDecimal.TEN;

        assertThat(converter.convert(value, unit, unit)).isSameAs(value);
    }

    @Test
    void convert_betweenUnits_shouldConvertThroughBaseUnit() {
        UnitUpdateDto inches = unit(1L, "2.54", "0");
        UnitUpdateDto centimeters = unit(1L, "1", "0");

        assertThat(converter.convert(BigDecimal.TEN, inches, centimeters))
                .isEqualByComparingTo("25.4");
    }

    @Test
    void convert_withOffsets_shouldApplySourceAndTargetOffsets() {
        UnitUpdateDto source = unit(1L, "2", "3");
        UnitUpdateDto target = unit(1L, "4", "1");

        assertThat(converter.convert(BigDecimal.TEN, source, target))
                .isEqualByComparingTo("5.5");
    }

    @Test
    void convert_betweenDifferentDimensions_shouldThrowLocalizedError() {
        UnitUpdateDto meters = unit(1L, "1", "0");
        UnitUpdateDto kilograms = unit(2L, "1", "0");
        when(localizationService.msg("error.differentUnitDimension")).thenReturn("Incompatible units");

        assertThatThrownBy(() -> converter.convert(BigDecimal.ONE, meters, kilograms))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Incompatible units");
    }

    private UnitUpdateDto unit(Long dimensionId, String factor, String offset) {
        return new UnitUpdateDto(
                null,
                true,
                "UNIT",
                "Unit",
                dimensionId,
                true,
                new BigDecimal(factor),
                new BigDecimal(offset));
    }
}
