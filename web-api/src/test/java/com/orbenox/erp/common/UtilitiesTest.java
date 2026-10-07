package com.orbenox.erp.common;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UtilitiesTest {

    @Test
    void getMessage_shouldReturnDeepestNonBlankCauseMessage() {
        Exception exception = new Exception("outer",
                new IllegalStateException("middle", new IllegalArgumentException("root cause")));

        assertThat(Utilities.getMessage(exception)).isEqualTo("root cause");
    }

    @Test
    void getMessage_shouldFallBackToCauseDescriptionWhenMessageIsBlank() {
        Exception exception = new Exception("outer", new IllegalArgumentException("  "));

        assertThat(Utilities.getMessage(exception)).isEqualTo("java.lang.IllegalArgumentException:   ");
    }

    @Test
    void isBlank_shouldHandleNullEmptyWhitespaceAndText() {
        assertThat(Utilities.isBlank(null)).isTrue();
        assertThat(Utilities.isBlank(" \t\n")).isTrue();
        assertThat(Utilities.isBlank(" value ")).isFalse();
    }

    @Test
    void writeErrorResponse_shouldSetHttpMetadataAndSerializeErrorEnvelope() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        Utilities.writeErrorResponse(response, 422, "Invalid input");

        verify(response).setStatus(422);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        assertThat(body).hasToString("{\"success\":false,\"code\":422,\"message\":\"Invalid input\",\"messageKey\":\"\",\"headers\":null,\"data\":null}");
    }
}
