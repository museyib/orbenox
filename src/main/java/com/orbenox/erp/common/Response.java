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
    private T data;

    public static <T> Response<T> successData(T data) {
        return new Response<>(HttpStatus.OK.value(), "success", data);
    }

    public static <T> Response<T> successMessage(String message) {
        return new Response<>(HttpStatus.OK.value(), message, null);
    }

    public static <T> Response<T> errorData(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

    public static <T> Response<T> errorMessage(int code, String message) {
        return new Response<>(code, message, null);
    }
}
