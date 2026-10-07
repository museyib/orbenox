package com.orbenox.erp.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BarcodeValidatorTest {

    private final BarcodeValidator.BarcodeValidatorImpl validator = new BarcodeValidator.BarcodeValidatorImpl();

    @Test
    void isValid_shouldAllowNullAndBlankValues() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void isValid_shouldAcceptAlphaNumericBarcodeWithHyphen() {
        assertThat(validator.isValid("ABC-123", null)).isTrue();
    }

    @Test
    void isValid_shouldRejectInvalidCharactersAndShortValues() {
        assertThat(validator.isValid("A@", null)).isFalse();
        assertThat(validator.isValid("AB", null)).isFalse();
    }

    @Test
    void isValid_shouldRejectValuesLongerThanFiftyCharacters() {
        assertThat(validator.isValid("123456789012345678901234567890123456789012345678901", null)).isFalse();
    }
}
