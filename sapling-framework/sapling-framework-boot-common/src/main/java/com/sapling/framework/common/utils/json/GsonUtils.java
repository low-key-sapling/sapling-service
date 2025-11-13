package com.sapling.framework.common.utils.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sapling.framework.common.exception.BasicException;
import com.sapling.framework.common.exception.enums.AppHttpStatus;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Gson 工具类
 * 提供常用的 JSON 序列化和反序列化方法
 *
 * @author Sapling
 * @version 1.0
 */
public class GsonUtils {

    /**
     * 默认的 Gson 实例（序列化 null 值）
     */
    private static final Gson GSON;

    /**
     * 格式化输出的 Gson 实例（序列化 null 值）
     */
    private static final Gson GSON_PRETTY;

    /**
     * 不序列化 null 值的 Gson 实例
     */
    private static final Gson GSON_NO_NULLS;

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    static {
        // 初始化默认 Gson（序列化 null 值）
        GSON = createGsonBuilder()
                .serializeNulls()
                .create();

        // 初始化格式化 Gson（序列化 null 值）
        GSON_PRETTY = createGsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        // 初始化不序列化 null 的 Gson（Gson 默认不序列化 null）
        GSON_NO_NULLS = createGsonBuilder()
                .create();
    }

    /**
     * 创建 GsonBuilder 并配置常用设置
     */
    private static GsonBuilder createGsonBuilder() {
        return new GsonBuilder()
                // 禁用 HTML 转义
                .disableHtmlEscaping()
                // 日期格式
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                // 注册 LocalDateTime 适配器
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                // 注册 LocalDate 适配器
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                // 注册 LocalTime 适配器
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter());
    }


    // ==================== 对象转 JSON 字符串 ====================

    /**
     * 将对象转换为 JSON 字符串（包含 null 值字段）
     *
     * @param obj 要转换的对象
     * @return JSON 字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return GSON.toJson(obj);
        } catch (Exception e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "对象转换为 JSON 字符串失败: " + e.getMessage());
        }
    }

    /**
     * 将对象转换为格式化的 JSON 字符串（带缩进，包含 null 值字段）
     *
     * @param obj 要转换的对象
     * @return 格式化的 JSON 字符串
     */
    public static String toJsonStringPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return GSON_PRETTY.toJson(obj);
        } catch (Exception e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "对象转换为格式化 JSON 字符串失败: " + e.getMessage());
        }
    }

    /**
     * 将对象转换为 JSON 字符串（不包含值为 null 的字段）
     *
     * @param obj 要转换的对象
     * @return JSON 字符串（不包含值为 null 的字段）
     */
    public static String toJsonStringNoNulls(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return GSON_NO_NULLS.toJson(obj);
        } catch (Exception e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "对象转换为 JSON 字符串失败: " + e.getMessage());
        }
    }

    /**
     * 将对象转换为 JSON 字符串，指定类型
     *
     * @param obj  要转换的对象
     * @param type 对象类型
     * @return JSON 字符串
     */
    public static String toJson(Object obj, Type type) {
        if (obj == null) {
            return null;
        }
        try {
            return GSON.toJson(obj, type);
        } catch (Exception e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "对象转换为 JSON 字符串失败: " + e.getMessage());
        }
    }


    // ==================== JSON 字符串转对象 ====================

    /**
     * 将 JSON 字符串转换为对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "JSON 字符串转换为对象失败: " + e.getMessage());
        }
    }

    /**
     * 将 JSON 字符串转换为对象，指定类型
     *
     * @param json JSON 字符串
     * @param type 目标类型
     * @param <T>  泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Type type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "JSON 字符串转换为对象失败: " + e.getMessage());
        }
    }

    /**
     * 将 JSON 字符串转换为对象，使用 TypeToken
     *
     * @param json      JSON 字符串
     * @param typeToken TypeToken 对象
     * @param <T>       泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, typeToken.getType());
        } catch (JsonSyntaxException e) {
            throw new BasicException(AppHttpStatus.JSON_PARSE_EXCEPTION.getStatus(),
                    "JSON 字符串转换为对象失败: " + e.getMessage());
        }
    }


    // ==================== JSON 字符串转 Map ====================

    /**
     * 将 JSON 字符串转换为 Map<String, Object>
     *
     * @param json JSON 字符串
     * @return Map 对象
     */
    public static Map<String, Object> toMap(String json) {
        return fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     * 将 JSON 字符串转换为 Map<String, String>
     *
     * @param json JSON 字符串
     * @return Map 对象
     */
    public static Map<String, String> toMapString(String json) {
        return fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    /**
     * 将 JSON 字符串转换为指定类型的 Map
     *
     * @param json       JSON 字符串
     * @param keyClass   Key 的类型
     * @param valueClass Value 的类型
     * @param <K>        Key 泛型
     * @param <V>        Value 泛型
     * @return Map 对象
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();
        return fromJson(json, type);
    }

    /**
     * 将 JSON 字符串转换为 HashMap
     *
     * @param json JSON 字符串
     * @return HashMap 对象
     */
    public static HashMap<String, Object> toHashMap(String json) {
        return fromJson(json, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

    /**
     * 将 JSON 字符串转换为 LinkedHashMap（保持顺序）
     *
     * @param json JSON 字符串
     * @return LinkedHashMap 对象
     */
    public static LinkedHashMap<String, Object> toLinkedHashMap(String json) {
        return fromJson(json, new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType());
    }


    // ==================== JSON 字符串转 List ====================

    /**
     * 将 JSON 字符串转换为 List<Object>
     *
     * @param json JSON 字符串
     * @return List 对象
     */
    public static List<Object> toList(String json) {
        return fromJson(json, new TypeToken<List<Object>>() {
        }.getType());
    }

    /**
     * 将 JSON 字符串转换为指定类型的 List
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return List 对象
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return fromJson(json, type);
    }

    /**
     * 将 JSON 字符串转换为 ArrayList
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return ArrayList 对象
     */
    public static <T> ArrayList<T> toArrayList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return fromJson(json, type);
    }

    /**
     * 将 JSON 字符串转换为 LinkedList
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return LinkedList 对象
     */
    public static <T> LinkedList<T> toLinkedList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(LinkedList.class, clazz).getType();
        return fromJson(json, type);
    }


    // ==================== JSON 字符串转 Set ====================

    /**
     * 将 JSON 字符串转换为 Set
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return Set 对象
     */
    public static <T> Set<T> toSet(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(Set.class, clazz).getType();
        return fromJson(json, type);
    }

    /**
     * 将 JSON 字符串转换为 HashSet
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return HashSet 对象
     */
    public static <T> HashSet<T> toHashSet(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(HashSet.class, clazz).getType();
        return fromJson(json, type);
    }

    /**
     * 将 JSON 字符串转换为 LinkedHashSet（保持顺序）
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return LinkedHashSet 对象
     */
    public static <T> LinkedHashSet<T> toLinkedHashSet(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(LinkedHashSet.class, clazz).getType();
        return fromJson(json, type);
    }

    // ==================== 复杂类型转换 ====================

    /**
     * 将 JSON 字符串转换为 List<Map<String, Object>>
     *
     * @param json JSON 字符串
     * @return List<Map < String, Object>> 对象
     */
    public static List<Map<String, Object>> toListMap(String json) {
        return fromJson(json, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
    }

    /**
     * 将 JSON 字符串转换为 Map<String, List<T>>
     *
     * @param json  JSON 字符串
     * @param clazz List 元素类型
     * @param <T>   泛型类型
     * @return Map<String, List < T>> 对象
     */
    public static <T> Map<String, List<T>> toMapList(String json, Class<T> clazz) {
        Type listType = TypeToken.getParameterized(List.class, clazz).getType();
        Type mapType = TypeToken.getParameterized(Map.class, String.class, listType).getType();
        return fromJson(json, mapType);
    }


    // ==================== 文件操作 ====================

    /**
     * 将对象写入 JSON 文件
     *
     * @param obj  要写入的对象
     * @param file 目标文件
     */
    public static void toFile(Object obj, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(),
                    "写入 JSON 文件失败: " + e.getMessage());
        }
    }

    /**
     * 将对象写入 JSON 文件（格式化）
     *
     * @param obj  要写入的对象
     * @param file 目标文件
     */
    public static void toFilePretty(Object obj, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON_PRETTY.toJson(obj, writer);
        } catch (IOException e) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(),
                    "写入 JSON 文件失败: " + e.getMessage());
        }
    }

    /**
     * 从 JSON 文件读取对象
     *
     * @param file  JSON 文件
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 读取的对象
     */
    public static <T> T fromFile(File file, Class<T> clazz) {
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(),
                    "读取 JSON 文件失败: " + e.getMessage());
        }
    }

    /**
     * 从 JSON 文件读取对象，指定类型
     *
     * @param file JSON 文件
     * @param type 目标类型
     * @param <T>  泛型类型
     * @return 读取的对象
     */
    public static <T> T fromFile(File file, Type type) {
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(),
                    "读取 JSON 文件失败: " + e.getMessage());
        }
    }


    // ==================== 流操作 ====================

    /**
     * 从输入流读取对象
     *
     * @param inputStream 输入流
     * @param clazz       目标类型
     * @param <T>         泛型类型
     * @return 读取的对象
     */
    public static <T> T fromInputStream(InputStream inputStream, Class<T> clazz) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(),
                    "从输入流读取对象失败: " + e.getMessage());
        }
    }

    /**
     * 将对象写入输出流
     *
     * @param obj          要写入的对象
     * @param outputStream 输出流
     */
    public static void toOutputStream(Object obj, OutputStream outputStream) {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            GSON.toJson(obj, writer);
            writer.flush();
        } catch (IOException e) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(),
                    "写入输出流失败: " + e.getMessage());
        }
    }

    // ==================== JsonElement 操作 ====================

    /**
     * 将对象转换为 JsonElement
     *
     * @param obj 要转换的对象
     * @return JsonElement 对象
     */
    public static JsonElement toJsonElement(Object obj) {
        return GSON.toJsonTree(obj);
    }

    /**
     * 将 JsonElement 转换为对象
     *
     * @param element JsonElement 对象
     * @param clazz   目标类型
     * @param <T>     泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJsonElement(JsonElement element, Class<T> clazz) {
        return GSON.fromJson(element, clazz);
    }

    /**
     * 将 JSON 字符串解析为 JsonElement
     *
     * @param json JSON 字符串
     * @return JsonElement 对象
     */
    public static JsonElement parseJsonElement(String json) {
        return JsonParser.parseString(json);
    }


    // ==================== 对象克隆 ====================

    /**
     * 深度克隆对象（通过 JSON 序列化和反序列化）
     *
     * @param obj   要克隆的对象
     * @param clazz 对象类型
     * @param <T>   泛型类型
     * @return 克隆后的对象
     */
    public static <T> T clone(T obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        String json = toJsonString(obj);
        return fromJson(json, clazz);
    }

    /**
     * 对象类型转换（通过 JSON 序列化和反序列化）
     *
     * @param obj         源对象
     * @param targetClass 目标类型
     * @param <S>         源类型
     * @param <T>         目标类型
     * @return 转换后的对象
     */
    public static <S, T> T convert(S obj, Class<T> targetClass) {
        if (obj == null) {
            return null;
        }
        String json = toJsonString(obj);
        return fromJson(json, targetClass);
    }

    /**
     * List 类型转换
     *
     * @param list        源 List
     * @param targetClass 目标元素类型
     * @param <S>         源元素类型
     * @param <T>         目标元素类型
     * @return 转换后的 List
     */
    public static <S, T> List<T> convertList(List<S> list, Class<T> targetClass) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        String json = toJsonString(list);
        return toList(json, targetClass);
    }

    // ==================== 工具方法 ====================

    /**
     * 判断字符串是否为有效的 JSON
     *
     * @param json 待判断的字符串
     * @return true 如果是有效的 JSON，否则 false
     */
    public static boolean isValidJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            JsonParser.parseString(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为 JSON 对象
     *
     * @param json 待判断的字符串
     * @return true 如果是 JSON 对象，否则 false
     */
    public static boolean isJsonObject(String json) {
        if (!isValidJson(json)) {
            return false;
        }
        JsonElement element = JsonParser.parseString(json);
        return element.isJsonObject();
    }

    /**
     * 判断字符串是否为 JSON 数组
     *
     * @param json 待判断的字符串
     * @return true 如果是 JSON 数组，否则 false
     */
    public static boolean isJsonArray(String json) {
        if (!isValidJson(json)) {
            return false;
        }
        JsonElement element = JsonParser.parseString(json);
        return element.isJsonArray();
    }


    /**
     * 格式化 JSON 字符串
     *
     * @param json 未格式化的 JSON 字符串
     * @return 格式化后的 JSON 字符串
     */
    public static String formatJson(String json) {
        if (StringUtils.isBlank(json)) {
            return json;
        }
        JsonElement element = JsonParser.parseString(json);
        return GSON_PRETTY.toJson(element);
    }

    /**
     * 压缩 JSON 字符串（移除空格和换行）
     *
     * @param json 格式化的 JSON 字符串
     * @return 压缩后的 JSON 字符串
     */
    public static String compressJson(String json) {
        if (StringUtils.isBlank(json)) {
            return json;
        }
        JsonElement element = JsonParser.parseString(json);
        return GSON.toJson(element);
    }

    /**
     * 获取自定义的 Gson 实例
     *
     * @return Gson 实例
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * 获取格式化的 Gson 实例
     *
     * @return Gson 实例
     */
    public static Gson getGsonPretty() {
        return GSON_PRETTY;
    }

    /**
     * 创建自定义的 GsonBuilder
     *
     * @return GsonBuilder 实例
     */
    public static GsonBuilder newGsonBuilder() {
        return createGsonBuilder();
    }


    // ==================== 内部类：类型适配器 ====================

    /**
     * LocalDateTime 类型适配器
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_TIME_FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateTimeStr = in.nextString();
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        }
    }

    /**
     * LocalDate 类型适配器
     */
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_FORMATTER));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        }
    }

    /**
     * LocalTime 类型适配器
     */
    private static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(TIME_FORMATTER));
            }
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String timeStr = in.nextString();
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        }
    }
}
