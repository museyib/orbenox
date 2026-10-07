package com.orbenox.erp.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotencyExceptionTest {

    @Test
    void constructor_shouldPreserveMessage() {
        IdempotencyException exception = new IdempotencyException("Duplicate request");

        assertThat(exception).hasMessage("Duplicate request");
    }
}
