package com.sapling.framework.elasticsearch.service;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.sapling.framework.elasticsearch.annotations.ESDsl;
import com.sapling.framework.elasticsearch.annotations.ESMapping;
import com.sapling.framework.elasticsearch.enums.ESMappingType;
import com.sapling.framework.elasticsearch.helper.LambdaHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * @author 小工匠
 * @version 1.0
 * @description: 抽象类解析泛型
 * @date 2022/4/4 23:01
 * @mark: show me the code , change the world
 */
public abstract class AbstractElasticBaseService<T> {

    {   //初始化解析
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        Class<T> clazz = (Class<T>) pt.getActualTypeArguments()[0];
        parseMapping(clazz);
    }

    /**
     * 索引名称
     */
    protected String indexName;
    /**
     * 索引模板名称
     */
    protected String indexTemplateName;
    /**
     * 索引类型
     */
    protected String indexType;
    /**
     * es写dsl的文件路径
     */
    protected String xmlPath;
    /**
     * 索引映射
     */
    protected String mapping;

    /**
     * 将Class解析成映射JSONString
     *
     * @param clazz
     */
    private void parseMapping(Class<T> clazz) {
        if (clazz.isAnnotationPresent(ESDsl.class)) {
            ESDsl esDsl = clazz.getAnnotation(ESDsl.class);
            this.xmlPath = esDsl.value();
            this.indexName = esDsl.indexName();
            this.indexTemplateName = esDsl.indexName() + "_template";
            //如果类型为空,则采用索引名作为其类型
            this.indexType = StrUtil.isBlank(esDsl.indexType()) ? esDsl.indexName() : esDsl.indexType();
        } else {
            throw new RuntimeException(clazz.getName() + "缺失注解[@ESDsl]");
        }
        //构建索引映射
        LambdaHashMap<Object, Object> put = LambdaHashMap.builder()
                .put("mappings", () -> LambdaHashMap.builder()
                        .put("properties", () -> {
                            Field[] fields = clazz.getDeclaredFields();
                            LambdaHashMap<Object, Object> builder = LambdaHashMap.builder();
                            for (Field field : fields) {
                                builder.put(field.getName(), () -> toEsjson(field));
                            }
                            return builder;
                        }));
        this.mapping = JSON.toJSONString(put);
    }

    /**
     * 构建JSON
     *
     * @param field
     * @return
     */
    private LambdaHashMap<Object, Object> toEsjson(Field field) {
        //基本数据类型
        if (ClassUtil.isSimpleTypeOrArray(field.getType())) {
            //对字符串做大小限制、分词设置
            if (new ArrayList<Class>(Collections.singletonList(String.class)).contains(field.getType())) {

                LambdaHashMap<Object, Object> put = null;
                // 如果是keyword类型
                if (field.isAnnotationPresent(ESMapping.class) && ESMappingType.keyword.getValue().equals(String.valueOf(field.getAnnotation(ESMapping.class).value()))) {
                    put = LambdaHashMap.builder()
                            .put("type", () -> "keyword");
                } else { // text类型
                    put = LambdaHashMap.builder()
                            .put("type", () -> "text")
                            .put("fields", () -> LambdaHashMap.builder()
                                    .put("keyword", () -> LambdaHashMap.builder()
                                            .put("type", () -> "keyword")
                                            .put("ignore_above", () -> 256)));
                }

                if (field.isAnnotationPresent(ESMapping.class)) {
                    ESMapping esMapping = field.getAnnotation(ESMapping.class);
                    //设置聚合分组
                    if (esMapping.fielddata()) {
                        put.put("fielddata", () -> true);
                    }
                    //设置加权
                    if (esMapping.boost() != 1) {
                        put.put("boost", esMapping::boost);
                    }
                    //设置是否进行分词
                    if (!"analyzed".equals(esMapping.index())) {
                        put.put("analyzed", esMapping::analyzer);
                    }

                    if (!ESMappingType.keyword.getValue().equals(String.valueOf(field.getAnnotation(ESMapping.class).value()))) {
                        //分词器
                        put.put("analyzer", esMapping::analyzer);
                    }

                }
                return put;
            }
            //设置默认类型
            return LambdaHashMap.builder().put("type", () -> {
                if (field.isAnnotationPresent(ESMapping.class)) {
                    ESMapping esMapping = field.getAnnotation(ESMapping.class);
                    return esMapping.value().getValue();
                }
                if (new ArrayList<Class>(Arrays.asList(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class)).contains(field.getType())) {
                    return "long";
                } else if (new ArrayList<Class>(Arrays.asList(double.class, Double.class, float.class, Float.class)).contains(field.getType())) {
                    return "double";
                } else if (new ArrayList<Class>(Arrays.asList(Date.class, java.sql.Date.class, LocalDate.class, LocalDateTime.class, LocalTime.class)).contains(field.getType())) {
                    return "date";
                } else if (new ArrayList<Class>(Arrays.asList(boolean.class, Boolean.class)).contains(field.getType())) {
                    return "boolean";
                }
                return "text";
            });
        } else {
            //设置对象类型
            LambdaHashMap<Object, Object> properties = LambdaHashMap.builder()
                    .put("properties", () -> {
                        Field[] fields = field.getType().getDeclaredFields();
                        LambdaHashMap<Object, Object> builder = LambdaHashMap.builder();
                        for (Field field01 : fields) {
                            builder.put(field01.getName(), toEsjson(field01));
                        }
                        return builder;
                    });
            if (field.isAnnotationPresent(ESMapping.class)) {
                ESMapping esMapping = field.getAnnotation(ESMapping.class);
                properties.put("type", esMapping.value().getValue());
            }
            return properties;
        }
    }
}
    