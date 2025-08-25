package com.wiinvent.checkinservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode readTree(String jsonString) throws JsonProcessingException {
        return MAPPER.readTree(jsonString);
    }
}
