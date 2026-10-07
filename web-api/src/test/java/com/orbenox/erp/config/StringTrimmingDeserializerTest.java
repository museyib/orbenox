package com.orbenox.erp.config;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StringTrimmingDeserializerTest {

    private final StringTrimmingDeserializer deserializer = new StringTrimmingDeserializer();
    private final JsonParser jsonParser = mock(JsonParser.class);

    @Test
    void deserialize_shouldTrimNonEmptyText() throws Exception {
        when(jsonParser.getText()).thenReturn("  value  ");

        assertThat(deserializer.deserialize(jsonParser, null)).isEqualTo("value");
    }

    @Test
    void deserialize_whenTextIsEmpty_shouldReturnNull() throws Exception {
        when(jsonParser.getText()).thenReturn("");

        assertThat(deserializer.deserialize(jsonParser, null)).isNull();
    }

    @Test
    void deserialize_whenTextIsNull_shouldReturnNull() throws Exception {
        when(jsonParser.getText()).thenReturn(null);

        assertThat(deserializer.deserialize(jsonParser, null)).isNull();
    }
}
