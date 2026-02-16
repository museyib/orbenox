package com.orbenox.erp.exception;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

import static com.orbenox.erp.common.Utilities.getMessage;
import static io.micrometer.common.util.StringUtils.isEmpty;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final LocalizationService i18n;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> handleException(Exception e) {
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = MessageFormat.format("{0}: {1}", i18n.msg("error.internal"), getMessage(e));
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message, "error.internal"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<String>> handleException(AccessDeniedException e) {
        int code = HttpStatus.FORBIDDEN.value();
        String message = MessageFormat.format("{0}: {1}", i18n.msg("error.forbidden"), getMessage(e));
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message, "error.forbidden"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<String>> handleException(BadCredentialsException e) {
        int code = HttpStatus.UNAUTHORIZED.value();
        String message = MessageFormat.format("{0}: {1}", i18n.msg("error.unauthorized"), getMessage(e));
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message, "error.unauthorized"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<String>> handleException(IllegalStateException e) {
        int code = HttpStatus.BAD_REQUEST.value();
        String message = MessageFormat.format("{0}: {1}", i18n.msg("error.validation"), getMessage(e));
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message, "error.validation"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fe -> isEmpty(fe.getDefaultMessage()) ? "" : fe.getDefaultMessage(),
                        (a, b) -> a));
        int code = HttpStatus.BAD_REQUEST.value();
        String message = MessageFormat.format("{0}: {1}", i18n.msg("error.validation"), errors);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorData(code, message, "error.validation", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<String>> handleException(DataIntegrityViolationException e) {
        int code = HttpStatus.BAD_REQUEST.value();
        String message = MessageFormat.format("{0}: {1}", i18n.msg("error.validation"), getMessage(e));
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message, "error.validation"));
    }
}
