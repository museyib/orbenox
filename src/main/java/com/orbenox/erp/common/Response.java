package com.orbenox.erp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private int code;
    private String message;
    private String messageKey;
    private T data;

    public static <T> Response<T> successData(T data) {
        return new Response<>(HttpStatus.OK.value(), "success", "", data);
    }

    public static <T> Response<T> successMessage(String message, String messageKey) {
        return new Response<>(HttpStatus.OK.value(), message, messageKey, null);
    }

    public static <T> Response<T> errorData(int code, String message, String messageKey, T data) {
        return new Response<>(code, message, messageKey, data);
    }

    public static <T> Response<T> errorMessage(int code, String message) {
        return new Response<>(code, message, "", null);
    }

    public static <T> Response<T> errorMessage(int code, String message, String messageKey) {
        return new Response<>(code, message, messageKey, null);
    }
}
