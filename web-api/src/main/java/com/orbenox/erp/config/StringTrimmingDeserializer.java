package com.orbenox.erp.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JacksonComponent;

import java.io.IOException;

@JacksonComponent
public class StringTrimmingDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();
        return (value == null || value.isEmpty()) ? null : value.trim();
    }
}
