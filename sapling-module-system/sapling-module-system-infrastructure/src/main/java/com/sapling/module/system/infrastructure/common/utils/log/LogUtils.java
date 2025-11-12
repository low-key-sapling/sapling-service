package com.sapling.module.system.infrastructure.common.utils.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 日志工具类
 *
 * @author zf
 * @since 2024-03-21
 */
@Slf4j
public class LogUtils {
    
    private LogUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 安全地记录debug日志
     * 在记录日志前会先检查debug级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void debug(Logger logger, String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }

    /**
     * 安全地记录debug日志（使用默认日志记录器）
     * 在记录日志前会先检查debug级别是否启用
     *
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void debug(String message, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(message, args);
        }
    }

    /**
     * 安全地记录debug日志（带异常信息）
     * 在记录日志前会先检查debug级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void debug(Logger logger, String message, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, throwable);
        }
    }

    /**
     * 安全地记录debug日志（带异常信息，使用默认日志记录器）
     * 在记录日志前会先检查debug级别是否启用
     *
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void debug(String message, Throwable throwable) {
        if (log.isDebugEnabled()) {
            log.debug(message, throwable);
        }
    }

    /**
     * 安全地记录info日志
     * 在记录日志前会先检查info级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void info(Logger logger, String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    /**
     * 安全地记录info日志（使用默认日志记录器）
     * 在记录日志前会先检查info级别是否启用
     *
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void info(String message, Object... args) {
        if (log.isInfoEnabled()) {
            log.info(message, args);
        }
    }

    /**
     * 安全地记录info日志（带异常信息）
     * 在记录日志前会先检查info级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void info(Logger logger, String message, Throwable throwable) {
        if (logger.isInfoEnabled()) {
            logger.info(message, throwable);
        }
    }

    /**
     * 安全地记录info日志（带异常信息，使用默认日志记录器）
     * 在记录日志前会先检查info级别是否启用
     *
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void info(String message, Throwable throwable) {
        if (log.isInfoEnabled()) {
            log.info(message, throwable);
        }
    }

    /**
     * 安全地记录warn日志
     * 在记录日志前会先检查warn级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void warn(Logger logger, String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, args);
        }
    }

    /**
     * 安全地记录warn日志（使用默认日志记录器）
     * 在记录日志前会先检查warn级别是否启用
     *
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void warn(String message, Object... args) {
        if (log.isWarnEnabled()) {
            log.warn(message, args);
        }
    }

    /**
     * 安全地记录warn日志（带异常信息）
     * 在记录日志前会先检查warn级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void warn(Logger logger, String message, Throwable throwable) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, throwable);
        }
    }

    /**
     * 安全地记录warn日志（带异常信息，使用默认日志记录器）
     * 在记录日志前会先检查warn级别是否启用
     *
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void warn(String message, Throwable throwable) {
        if (log.isWarnEnabled()) {
            log.warn(message, throwable);
        }
    }

    /**
     * 安全地记录error日志
     * 在记录日志前会先检查error级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void error(Logger logger, String message, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(message, args);
        }
    }

    /**
     * 安全地记录error日志（使用默认日志记录器）
     * 在记录日志前会先检查error级别是否启用
     *
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void error(String message, Object... args) {
        if (log.isErrorEnabled()) {
            log.error(message, args);
        }
    }

    /**
     * 安全地记录error日志（带异常信息）
     * 在记录日志前会先检查error级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void error(Logger logger, String message, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(message, throwable);
        }
    }

    /**
     * 安全地记录error日志（带异常信息，使用默认日志记录器）
     * 在记录日志前会先检查error级别是否启用
     *
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void error(String message, Throwable throwable) {
        if (log.isErrorEnabled()) {
            log.error(message, throwable);
        }
    }

    /**
     * 安全地记录trace日志
     * 在记录日志前会先检查trace级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void trace(Logger logger, String message, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace(message, args);
        }
    }

    /**
     * 安全地记录trace日志（使用默认日志记录器）
     * 在记录日志前会先检查trace级别是否启用
     *
     * @param message 日志消息
     * @param args 日志参数
     */
    public static void trace(String message, Object... args) {
        if (log.isTraceEnabled()) {
            log.trace(message, args);
        }
    }

    /**
     * 安全地记录trace日志（带异常信息）
     * 在记录日志前会先检查trace级别是否启用
     *
     * @param logger 日志记录器
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void trace(Logger logger, String message, Throwable throwable) {
        if (logger.isTraceEnabled()) {
            logger.trace(message, throwable);
        }
    }

    /**
     * 安全地记录trace日志（带异常信息，使用默认日志记录器）
     * 在记录日志前会先检查trace级别是否启用
     *
     * @param message 日志消息
     * @param throwable 异常信息
     */
    public static void trace(String message, Throwable throwable) {
        if (log.isTraceEnabled()) {
            log.trace(message, throwable);
        }
    }

    /**
     * 格式化日志消息
     * 支持多个占位符，例如：format("用户{}在{}登录", userId, time)
     *
     * @param message 日志消息模板
     * @param args 参数数组
     * @return 格式化后的消息
     */
    public static String format(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        return String.format(message.replace("{}", "%s"), args);
    }

    /**
     * 检查参数数量是否匹配占位符数量
     *
     * @param message 日志消息模板
     * @param args 参数数组
     * @return 是否匹配
     */
    public static boolean isArgsMatch(String message, Object... args) {
        if (args == null) {
            return message.indexOf("{}") == -1;
        }
        int placeholderCount = countPlaceholders(message);
        return placeholderCount == args.length;
    }

    /**
     * 计算消息中的占位符数量
     *
     * @param message 日志消息模板
     * @return 占位符数量
     */
    private static int countPlaceholders(String message) {
        int count = 0;
        int index = 0;
        while ((index = message.indexOf("{}", index)) != -1) {
            count++;
            index += 2;
        }
        return count;
    }
} 