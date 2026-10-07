package com.orbenox.erp.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PositivePriceValidatorTest {

    @Test
    void isValid_whenZeroIsNotAllowed_shouldRequirePositiveNonNullPrice() {
        PositivePrice constraint = mock(PositivePrice.class);
        when(constraint.allowZero()).thenReturn(false);
        PositivePrice.PositivePriceValidator validator = new PositivePrice.PositivePriceValidator();
        validator.initialize(constraint);

        assertThat(validator.isValid(BigDecimal.ONE, mock(ConstraintValidatorContext.class))).isTrue();
        assertThat(validator.isValid(BigDecimal.ZERO, mock(ConstraintValidatorContext.class))).isFalse();
        assertThat(validator.isValid(BigDecimal.ONE.negate(), mock(ConstraintValidatorContext.class))).isFalse();
        assertThat(validator.isValid(null, mock(ConstraintValidatorContext.class))).isFalse();
    }

    @Test
    void isValid_whenZeroIsAllowed_shouldRejectOnlyNegativeOrNullPrice() {
        PositivePrice constraint = mock(PositivePrice.class);
        when(constraint.allowZero()).thenReturn(true);
        PositivePrice.PositivePriceValidator validator = new PositivePrice.PositivePriceValidator();
        validator.initialize(constraint);

        assertThat(validator.isValid(BigDecimal.ONE, mock(ConstraintValidatorContext.class))).isTrue();
        assertThat(validator.isValid(BigDecimal.ZERO, mock(ConstraintValidatorContext.class))).isTrue();
        assertThat(validator.isValid(BigDecimal.ONE.negate(), mock(ConstraintValidatorContext.class))).isFalse();
        assertThat(validator.isValid(null, mock(ConstraintValidatorContext.class))).isFalse();
    }
}
