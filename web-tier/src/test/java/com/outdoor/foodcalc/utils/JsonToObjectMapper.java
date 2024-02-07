package com.outdoor.foodcalc.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonToObjectMapper {

    private static ObjectMapper objectMapper;

    private final String filePath = "./src/test/resources/test-data/";

    public JsonToObjectMapper(ObjectMapper objectMapper) {
        JsonToObjectMapper.objectMapper = objectMapper;
    }

    public <T> void serializeObject(String fileName, T object) throws IOException {
        objectMapper.writeValue(new File(filePath + fileName), object);
    }

    public <T> T deserializeObject(String fileName, Class<T> objectClass) throws IOException {
        return objectMapper.readValue(new File(filePath + fileName), objectClass);
    }
}
