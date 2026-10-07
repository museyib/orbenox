package com.orbenox.erp.idempotency;

import com.orbenox.erp.exception.IdempotencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.json.JsonMapper;

import static com.orbenox.erp.idempotency.IdempotencyRecord.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class IdempotencyExecutorTest {
    private IdempotencyService idempotencyService;
    private IdempotencyExecutor executor;
    private JsonMapper jsonMapper;

    private static IdempotencyRecord record(String hash, IdempotencyRecord.Status status) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setRequestHash(hash);
        record.setStatus(status);
        return record;
    }

    @BeforeEach
    void setUp() {
        idempotencyService = mock(IdempotencyService.class);
        jsonMapper = JsonMapper.builder().build();
        executor = new IdempotencyExecutor(idempotencyService, jsonMapper);
    }

    @Test
    void execute_shouldRunAndPersistTheFirstSuccessfulRequest() {
        when(idempotencyService.tryLock("key", "hash")).thenReturn(true);
        ResponseEntity<String> response = ResponseEntity.status(201).body("created");

        Object result = executor.execute("key", "hash", () -> response);

        assertThat(result).isSameAs(response);
        verify(idempotencyService).complete("key", 201, "hash", "created");
    }

    @Test
    void execute_shouldRejectPayloadConflictAndRequestsStillProcessing() {
        when(idempotencyService.tryLock("key", "hash")).thenReturn(false);
        IdempotencyRecord record = record("different", PROCESSING);
        when(idempotencyService.getRecord("key")).thenReturn(record);

        assertThatThrownBy(() -> executor.execute("key", "hash", () -> "unused"))
                .isInstanceOf(IdempotencyException.class)
                .hasMessageContaining("different payload");

        record.setRequestHash("hash");
        assertThatThrownBy(() -> executor.execute("key", "hash", () -> "unused"))
                .isInstanceOf(IdempotencyException.class)
                .hasMessage("Request is already being processed.");
    }

    @Test
    void execute_shouldReplayCompletedResponseAndRejectUnknownOrFailedRecords() {
        when(idempotencyService.tryLock("key", "hash")).thenReturn(false);
        IdempotencyRecord record = record("hash", COMPLETED);
        record.setResponseStatus(202);
        record.setResponseBody("{\"accepted\":true}");
        when(idempotencyService.getRecord("key")).thenReturn(record);

        Object replay = executor.execute("key", "hash", () -> "unused");

        assertThat(replay).isInstanceOf(ResponseEntity.class);
        ResponseEntity<?> entity = (ResponseEntity<?>) replay;
        assertThat(entity.getStatusCode().value()).isEqualTo(202);
        assertThat(entity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(entity.getBody().toString()).contains("\"accepted\":true");

        when(idempotencyService.getRecord("key")).thenReturn(record("hash", FAILED));
        assertThatThrownBy(() -> executor.execute("key", "hash", () -> "unused"))
                .isInstanceOf(IdempotencyException.class)
                .hasMessage("Concurrent request processing error.");
        when(idempotencyService.getRecord("key")).thenReturn(null);
        assertThatThrownBy(() -> executor.execute("key", "hash", () -> "unused"))
                .isInstanceOf(IdempotencyException.class)
                .hasMessage("Concurrent request processing error.");
    }
}
