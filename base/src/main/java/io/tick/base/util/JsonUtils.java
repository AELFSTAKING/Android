package io.tick.base.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {
    private static ObjectMapper mapper;

    private JsonUtils() {
    }

    @Nullable
    public static String toJsonString(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> T parse(String jsonString, Class<T> clsBean) {
        try {
            if (TextUtils.isEmpty(jsonString)) {
                return null;
            }
            return getObjectMapper().readValue(jsonString, clsBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        try {
            if (TextUtils.isEmpty(jsonString)) {
                return null;
            }
            return getObjectMapper().readValue(jsonString,
                    getCollectionType(ArrayList.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private static JavaType getCollectionType(Class<?> collectionClass,
            Class<?>... elementClasses) {
        return getObjectMapper().getTypeFactory().constructParametricType(collectionClass,
                elementClasses);
    }

    private static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            synchronized (JsonUtils.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    // 在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    // 在序列化时忽略值为 null 的属性
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                }
            }
        }
        return mapper;
    }
}
