package com.sapling.module.system.infrastructure.common.framework.security;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 承载一次请求的所有关键信息，供规则判断使用
 */
public class RequestContext {
    private final HttpServletRequest request;
    private final Map<String, String[]> parameterMap;
    private final String uri;
    private final String method;
    private final String remoteAddr;
    private final Map<String, String> headers;

    public RequestContext(HttpServletRequest request) {
        this.request = request;
        this.parameterMap = request.getParameterMap();
        this.uri = request.getRequestURI();
        this.method = request.getMethod();
        this.remoteAddr = request.getRemoteAddr();
        this.headers = new HashMap<>();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
} 