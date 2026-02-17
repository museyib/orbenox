package com.orbenox.erp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private boolean success;
    private int code;
    private String message;
    private String messageKey;
    private Map<String, Object> headers;
    private T data;

    public static <T> Response<T> successData(T data) {
        return new Response<>(true, HttpStatus.OK.value(), "success", "", null, data);
    }

    public static <T> Response<T> successDataWithHeaders(T data, Map<String, Object> headers) {
        return new Response<>(true, HttpStatus.OK.value(), "success", "", headers, data);
    }

    public static <T> Response<T> successMessage(String message, String messageKey) {
        return new Response<>(true, HttpStatus.OK.value(), message, messageKey, null, null);
    }

    public static <T> Response<T> errorData(int code, String message, String messageKey, T data) {
        return new Response<>(false, code, message, messageKey, null, data);
    }

    public static <T> Response<T> errorMessage(int code, String message) {
        return new Response<>(false, code, message, "", null, null);
    }

    public static <T> Response<T> errorMessage(int code, String message, String messageKey) {
        return new Response<>(false, code, message, messageKey, null, null);
    }
}
