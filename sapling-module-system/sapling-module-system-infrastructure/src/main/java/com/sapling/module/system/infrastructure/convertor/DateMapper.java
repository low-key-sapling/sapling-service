package com.sapling.module.system.infrastructure.convertor;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换工具类
 * 用于MapStruct映射中的日期字段转换
 *
 * @author lccc
 */
@Slf4j
public class DateMapper {

    /**
     * 默认日期格式
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将字符串转换为日期
     *
     * @param dateString 日期字符串
     * @return 日期对象，转换失败返回null
     */
    @Named("stringToDate")
    public static Date stringToDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            log.warn("日期转换失败: {}", dateString, e);
            return null;
        }
    }

    /**
     * 将日期转换为字符串
     *
     * @param date 日期对象
     * @return 日期字符串，转换失败返回null
     */
    @Named("dateToString")
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * 将Long类型的时间戳转换为Date对象
     *
     * @param timestamp 时间戳 (毫秒)
     * @return Date对象，如果时间戳为null则返回null
     * @author lccc
     */
    @Named("longToDate")
    public static Date longToDate(Long timestamp) {
        if (timestamp == null || timestamp <= 0) { // 0或负数时间戳通常无效
            return null;
        }
        return new Date(timestamp);
    }

    /**
     * 设置默认的Integer值
     * 如果原值为null，则返回默认值
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @return 处理后的值
     */
    @Named("defaultInteger")
    public static Integer defaultInteger(Integer value, Integer defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 设置judge_flag的默认值为0
     * 如果原值为null，则返回0
     *
     * @param value 原值
     * @return 处理后的值
     */
    @Named("defaultJudgeFlag")
    public static Integer defaultJudgeFlag(Integer value) {
        return defaultInteger(value, 0);
    }

    /**
     * 设置默认的Long值
     * 如果原值为null，则返回默认值
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @return 处理后的值
     */
    @Named("defaultLong")
    public static Long defaultLong(Long value, Long defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 设置默认的String值
     * 如果原值为null或空字符串，则返回默认值
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @return 处理后的值
     */
    @Named("defaultString")
    public static String defaultString(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * 设置默认的Boolean值
     * 如果原值为null，则返回默认值
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @return 处理后的值
     */
    @Named("defaultBoolean")
    public static Boolean defaultBoolean(Boolean value, Boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 泛型方法：为任意类型设置默认值
     * 如果原值为null，则返回默认值
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @param <T> 值的类型
     * @return 处理后的值
     */
    @Named("defaultValue")
    public static <T> T defaultValue(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
} 