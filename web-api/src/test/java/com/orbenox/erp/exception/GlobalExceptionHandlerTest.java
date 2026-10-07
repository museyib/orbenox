package com.orbenox.erp.exception;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private LocalizationService localizationService;
    private GlobalExceptionHandler handler;

    private static void assertError(org.springframework.http.ResponseEntity<Response<String>> response,
                                    HttpStatus status, String messageKey, String detail) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(status.value());
        assertThat(response.getBody().getMessageKey()).isEqualTo(messageKey);
        assertThat(response.getBody().getMessage()).contains(detail);
    }

    @BeforeEach
    void setUp() {
        localizationService = mock(LocalizationService.class);
        when(localizationService.msg(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        handler = new GlobalExceptionHandler(localizationService);
    }

    @Test
    void handleException_shouldMapGeneralAndSecurityFailuresToTheirHttpStatuses() {
        assertError(handler.handleException(new Exception("internal")), HttpStatus.INTERNAL_SERVER_ERROR,
                "error.internal", "internal");
        assertError(handler.handleException(new AccessDeniedException("denied")), HttpStatus.FORBIDDEN,
                "error.forbidden", "denied");
        assertError(handler.handleException(new BadCredentialsException("bad credentials")), HttpStatus.UNAUTHORIZED,
                "error.unauthorized", "bad credentials");
    }

    @Test
    void handleException_shouldMapValidationAndPersistenceFailuresToBadRequest() {
        assertError(handler.handleException(new IllegalStateException("invalid state")), HttpStatus.BAD_REQUEST,
                "error.validation", "invalid state");
        assertError(handler.handleException(new DataIntegrityViolationException("duplicate")), HttpStatus.BAD_REQUEST,
                "error.validation", "duplicate");
        assertError(handler.handleException(new BusinessRuleException("not allowed")), HttpStatus.BAD_REQUEST,
                "error.business", "not allowed");
    }

    @Test
    void handleException_shouldReturnFieldErrorsAndKeepFirstDuplicateFieldMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "name", "required"),
                new FieldError("request", "name", "different error"),
                new FieldError("request", "description", null, false, null, null, "  ")));

        var response = handler.handleException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Response<Map<String, String>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getData()).containsExactlyInAnyOrderEntriesOf(Map.of("name", "required", "description", ""));
        assertThat(body.getMessageKey()).isEqualTo("error.validation");
    }

    @Test
    void handleException_shouldMapIdempotencyConflictsToConflict() {
        assertError(handler.handleException(new IdempotencyException("duplicate key")), HttpStatus.CONFLICT,
                "error.conflict", "duplicate key");
        assertError(handler.handleException(
                        new IdempotencyException("duplicate request")),
                HttpStatus.CONFLICT, "error.conflict", "duplicate request");
    }
}
