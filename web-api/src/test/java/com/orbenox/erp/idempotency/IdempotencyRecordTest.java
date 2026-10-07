package com.orbenox.erp.idempotency;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotencyRecordTest {
    private static IdempotencyRecord record(long id) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setId(id);
        return record;
    }

    @Test
    void equalsAndHashCode_shouldUsePersistedRecordId() {
        IdempotencyRecord first = record(11L);
        IdempotencyRecord sameId = record(11L);
        IdempotencyRecord differentId = record(12L);

        assertThat(first).isEqualTo(sameId);
        assertThat(first.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(first).isNotEqualTo(differentId);
        assertThat(first.hashCode()).isNotEqualTo(differentId.hashCode());
        assertThat(first).isNotEqualTo(null).isNotEqualTo("record");
    }
}
