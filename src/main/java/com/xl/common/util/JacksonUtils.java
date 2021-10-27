package com.xl.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author xl
 * @date 2021年10月27日 13:20
 */
public class JacksonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtils.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public JacksonUtils() {
    }

    public static String write2JsonString(Object obj) {
        if (obj != null) {
            try {
                return mapper.writeValueAsString(obj);
            } catch (JsonProcessingException var2) {
                logger.error("write2JsonString error", var2);
            }
        }

        return null;
    }

    public static <T> T readJson2Entity(String jsonString, Class<T> clazz) {
        if (jsonString != null) {
            try {
                return mapper.readValue(jsonString, clazz);
            } catch (IOException var3) {
                logger.error("readJson2Entity error jsonString={},class={}", new Object[]{jsonString, clazz.getName(), var3});
            }
        }

        return null;
    }

    public static <T> List<T> readJson2EntityList(String jsonString, Class<T> clazz) {
        if (jsonString != null) {
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);

            try {
                return (List)mapper.readValue(jsonString, javaType);
            } catch (IOException var4) {
                logger.error("readJson2EntityList error jsonString={},class={}", new Object[]{jsonString, clazz.getName(), var4});
            }
        }

        return null;
    }

    public static <T> T readJson2EntityByTypeReference(String jsonString, TypeReference<T> valueTypeRef) {
        if (jsonString != null) {
            try {
                return mapper.readValue(jsonString, valueTypeRef);
            } catch (IOException var3) {
                logger.error("readJson2EntityByTypeReference error jsonString={},class={}", new Object[]{jsonString, valueTypeRef, var3});
            }
        }

        return null;
    }

    public static <V> Map<String, V> toMap(Object object) {
        return (Map)mapper.convertValue(object, new TypeReference<Map<String, V>>() {
        });
    }

    static {
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(new MapperFeature[]{MapperFeature.DEFAULT_VIEW_INCLUSION});
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
    }
}
