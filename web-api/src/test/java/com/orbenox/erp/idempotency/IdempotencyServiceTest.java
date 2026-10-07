package com.orbenox.erp.idempotency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

import static com.orbenox.erp.idempotency.IdempotencyRecord.Status.COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IdempotencyServiceTest {
    private IdempotencyRepository repository;
    private IdempotencyService service;

    @BeforeEach
    void setUp() {
        repository = mock(IdempotencyRepository.class);
        service = new IdempotencyService(repository, JsonMapper.builder().build());
    }

    @Test
    void tryLock_shouldReportWhetherInsertWonTheUniqueKeyRace() {
        when(repository.createIdempotency("key", "PROCESSING", "hash")).thenReturn(1, 0);

        assertThat(service.tryLock("key", "hash")).isTrue();
        assertThat(service.tryLock("key", "hash")).isFalse();
    }

    @Test
    void getRecord_shouldReturnNullWhenKeyDoesNotExist() {
        when(repository.findByIdempotencyKey("missing")).thenReturn(Optional.empty());

        assertThat(service.getRecord("missing")).isNull();
    }

    @Test
    void complete_shouldUpdateAndSaveExistingRecordWithSerializedResponse() {
        IdempotencyRecord record = new IdempotencyRecord();
        when(repository.findByIdempotencyKey("key")).thenReturn(Optional.of(record));

        service.complete("key", 201, "request-hash", java.util.Map.of("id", 9));

        assertThat(record.getStatus()).isEqualTo(COMPLETED);
        assertThat(record.getRequestHash()).isEqualTo("request-hash");
        assertThat(record.getResponseStatus()).isEqualTo(201);
        assertThat(record.getResponseBody()).contains("\"id\":9");
        verify(repository).save(record);
    }

    @Test
    void complete_shouldNotSaveWhenTheRecordHasDisappeared() {
        when(repository.findByIdempotencyKey("deleted")).thenReturn(Optional.empty());

        service.complete("deleted", 200, "hash", null);

        verify(repository, never()).save(any());
    }
}
