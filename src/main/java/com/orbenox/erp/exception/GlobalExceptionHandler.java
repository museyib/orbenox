package com.orbenox.erp.exception;

import com.orbenox.erp.common.Response;
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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.orbenox.erp.common.Utilities.getMessage;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> handleException(Exception e) {
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = getMessage(e);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<String>> handleException(AccessDeniedException e) {
        int code = HttpStatus.FORBIDDEN.value();
        String message = getMessage(e);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<String>> handleException(BadCredentialsException e) {
        int code = HttpStatus.UNAUTHORIZED.value();
        String message = getMessage(e);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<String>> handleException(IllegalStateException e) {
        int code = HttpStatus.BAD_REQUEST.value();
        String message = getMessage(e);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fe -> Optional.ofNullable(fe.getDefaultMessage()).orElse("Invalid value: " + fe.getRejectedValue()),
                        (a, b) -> a));
        int code = HttpStatus.BAD_REQUEST.value();
        String message = getMessage(e);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorData(code, message, errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<String>> handleException(DataIntegrityViolationException e) {
        int code = HttpStatus.BAD_REQUEST.value();
        String message = getMessage(e);
        log.error(message);
        return ResponseEntity.status(code).body(Response.errorMessage(code, message));
    }
}
