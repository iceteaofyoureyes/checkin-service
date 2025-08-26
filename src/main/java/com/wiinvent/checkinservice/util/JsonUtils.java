package com.wiinvent.checkinservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode readTree(String jsonString) throws JsonProcessingException {
        return MAPPER.readTree(jsonString);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
            return MAPPER.readValue(json, clazz);
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonProcessingException {
            return MAPPER.readValue(json, typeReference);
    }

    public static String toJson(Object object) throws JsonProcessingException {
            return MAPPER.writeValueAsString(object);
    }

}
