package com.orbenox.erp.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Utilities {
    public static String getMessage(Exception e) {
        String message;
        Throwable throwable = e;
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        message = throwable.getMessage();
        if (isEmpty(message)) {
            message = throwable.toString();
        }

        return message;
    }

    private static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static void writeErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        Response<String> customResponse = Response.errorMessage(code, message);
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(code);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(customResponse));
    }
}
