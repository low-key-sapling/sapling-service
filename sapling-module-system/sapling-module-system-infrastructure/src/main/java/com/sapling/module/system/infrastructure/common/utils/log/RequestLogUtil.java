package com.sapling.module.system.infrastructure.common.utils.log;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.common.utils.servlet.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志工具类
 * 用于统一处理请求信息的记录
 */
@Slf4j
public class RequestLogUtil {

    /**
     * 获取请求的详细信息
     *
     * @param request HTTP请求对象
     * @return 包含请求详细信息的Map
     */
    public static Map<String, Object> getRequestDetails(HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        
        // 基本信息
        details.put("ip", ServletUtils.getClientIP());
        details.put("method", request.getMethod());
        details.put("uri", request.getRequestURI());
        details.put("url", request.getRequestURL().toString());
        
        // 请求头信息
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        details.put("headers", headers);
        
        // 请求参数
        Map<String, String[]> parameters = request.getParameterMap();
        Map<String, String> formattedParams = new HashMap<>();
        parameters.forEach((key, values) -> {
            if (values != null && values.length > 0) {
                formattedParams.put(key, String.join(",", values));
            }
        });
        details.put("parameters", formattedParams);

//        // 如果是JSON请求，获取请求体
//        if ("application/json".equals(request.getContentType())) {
//            try {
//                String body = getRequestBody(request);
//                if (ObjectUtil.isNotEmpty(body)) {
//                    details.put("requestBody", body);
//                }
//            } catch (IOException e) {
//                log.warn("读取请求体失败", e);
//            }
//        }
        
        return details;
    }

//    /**
//     * 获取请求体内容
//     *
//     * @param request HTTP请求对象
//     * @return 请求体内容
//     * @throws IOException 读取请求体失败时抛出
//     */
//    private static String getRequestBody(HttpServletRequest request) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        }
//        return sb.toString();
//    }

    /**
     * 记录请求信息
     *
     * @param request HTTP请求对象
     * @param message 附加信息
     */
    public static void logRequest(HttpServletRequest request, String message) {
        Map<String, Object> details = getRequestDetails(request);
        log.info("{} - IP: {}, 方法: {}, URL: {}, 请求头: {}, 参数: {}, 请求体: {}",
            message,
            details.get("ip"),
            details.get("method"),
            details.get("uri"),
            details.get("headers"),
            details.get("parameters"),
            details.get("requestBody"));
    }

    /**
     * 记录请求警告信息
     *
     * @param request HTTP请求对象
     * @param message 警告信息
     * @param additionalInfo 附加信息
     */
    public static void logRequestWarning(HttpServletRequest request, String message, Object... additionalInfo) {
        Map<String, Object> details = getRequestDetails(request);
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(message)
                 .append(" - IP: ")
                 .append(details.get("ip"))
                 .append(", URL: ")
                 .append(details.get("uri"));
        
        if (ObjectUtil.isNotEmpty(additionalInfo)) {
            for (Object info : additionalInfo) {
                logMessage.append(", ").append(info);
            }
        }
        
        logMessage.append(", 请求头: ")
                 .append(details.get("headers"))
                 .append(", 参数: ")
                 .append(details.get("parameters"));
        
        if (details.containsKey("requestBody")) {
            logMessage.append(", 请求体: ")
                     .append(details.get("requestBody"));
        }
        
        log.warn(logMessage.toString());
    }

    /**
     * 记录请求调试信息
     *
     * @param request HTTP请求对象
     * @param message 调试信息
     * @param additionalInfo 附加信息
     */
    public static void logRequestDebug(HttpServletRequest request, String message, Object... additionalInfo) {
        Map<String, Object> details = getRequestDetails(request);
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(message)
                 .append(" - IP: ")
                 .append(details.get("ip"))
                 .append(", URL: ")
                 .append(details.get("uri"));
        
        if (ObjectUtil.isNotEmpty(additionalInfo)) {
            for (Object info : additionalInfo) {
                logMessage.append(", ").append(info);
            }
        }
        
        logMessage.append(", 请求头: ")
                 .append(details.get("headers"))
                 .append(", 参数: ")
                 .append(details.get("parameters"));
        
        if (details.containsKey("requestBody")) {
            logMessage.append(", 请求体: ")
                     .append(details.get("requestBody"));
        }
        
        log.debug(logMessage.toString());
    }
} 