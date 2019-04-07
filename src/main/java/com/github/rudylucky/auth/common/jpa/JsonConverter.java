package com.github.rudylucky.auth.common.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.rudylucky.auth.common.exception.CustomException;
import com.github.rudylucky.auth.common.util.JsonUtils;

import javax.persistence.AttributeConverter;

public class JsonConverter implements AttributeConverter<JsonNode, String> {

    public static final int LENGTH = 8000;

    @Override
    public String convertToDatabaseColumn(JsonNode n) {
        try {
            return JsonUtils.mapper.writeValueAsString(n);
        } catch (Exception e) {
            throw new CustomException(String.format("Failed to convert json node to string with error %s",
                    e.getMessage()));
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String s) {
        try {
            return JsonUtils.mapper.readTree(s);
        } catch (Exception e) {
            throw new CustomException(String.format("Failed to parse json string %s with error %s", s, e.getMessage()));
        }
    }
}
