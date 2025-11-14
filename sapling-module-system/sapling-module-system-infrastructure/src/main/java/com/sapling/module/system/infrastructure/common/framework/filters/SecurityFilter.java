package com.sapling.module.system.infrastructure.common.framework.filters;

import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.utils.log.RequestLogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sapling.framework.common.exception.BusinessException;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;

/**
 * 安全过滤器：在请求进入 Spring MVC 之前进行基础安全检查
 *
 * 基础安全检查：
 * - IP 黑名单
 * - Host 检查
 * - Content-Type 检查
 * - 请求大小限制
 * - 请求头大小限制
 * - 参数数量限制
 * - 请求方法检查
 */
@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    /**
     * endpoint:
     *   security:
     *     max:
     *       parameters: 1000      # 最大参数数量
     *       parameter.size: 4096  # 单个参数最大长度
     *     allowed:
     *       hosts: example.com,api.example.com  # 允许的 Host
     */

    @Value("${endpoint.security.enabled:true}")
    private boolean enabled;

    @Value("${endpoint.security.blacklist.ips:}")
    private Set<String> blacklistedIps = new HashSet<>();

    @Value("${endpoint.security.blocked.content-types:application/xml}")
    private Set<String> blockedContentTypes = new HashSet<>();

    @Value("${endpoint.security.max.request.size:10485760}")
    private long maxRequestSize; // 默认 10MB

    @Value("${endpoint.security.max.header.size:8192}")
    private int maxHeaderSize; // 默认 8KB

    @Value("${endpoint.security.max.parameters:1000}")
    private int maxParameters; // 默认最大参数数量

    @Value("${endpoint.security.max.parameter.size:4096}")
    private int maxParameterSize; // 默认单个参数最大长度

    @Value("${endpoint.security.allowed.hosts:}")
    private Set<String> allowedHosts = new HashSet<>();

    private SecurityHandler securityChain;

    @PostConstruct
    public void init() {
        // 构建安全检查责任链
        securityChain = new IpBlacklistHandler(blacklistedIps);
        securityChain.setNext(new HostValidationHandler(allowedHosts))
            .setNext(new ContentTypeHandler(blockedContentTypes))
            .setNext(new RequestSizeHandler(maxRequestSize))
//            .setNext(new HeaderSizeHandler(maxHeaderSize))  // 由中间件自身校验
            .setNext(new ParameterHandler(maxParameters, maxParameterSize))
            .setNext(new HttpMethodHandler());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        // 包装请求以支持多次读取请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        
        // 执行安全检查链
        securityChain.handle(requestWrapper);
        
        // 继续处理请求
        chain.doFilter(requestWrapper, response);
    }

    /**
     * 安全处理器抽象类
     */
    private abstract static class SecurityHandler {
        private SecurityHandler next;

        public SecurityHandler setNext(SecurityHandler next) {
            this.next = next;
            return next;
        }

        public void handle(HttpServletRequest request) {
            doHandle(request);
            if (next != null) {
                next.handle(request);
            }
        }

        protected abstract void doHandle(HttpServletRequest request);
    }

    /**
     * IP 黑名单处理器
     */
    private static class IpBlacklistHandler extends SecurityHandler {
        private final Set<String> blacklistedIps;

        public IpBlacklistHandler(Set<String> blacklistedIps) {
            this.blacklistedIps = blacklistedIps;
        }

        @Override
        protected void doHandle(HttpServletRequest request) {
            String clientIp = request.getRemoteAddr();
            if (blacklistedIps.contains(clientIp)) {
                RequestLogUtil.logRequestWarning(request, "IP 被列入黑名单，阻断访问", "IP=" + clientIp);
                throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN.getType(), "IP已被禁止访问");
            }
        }
    }

    /**
     * Host 验证处理器
     */
    private static class HostValidationHandler extends SecurityHandler {
        private final Set<String> allowedHosts;

        public HostValidationHandler(Set<String> allowedHosts) {
            this.allowedHosts = allowedHosts;
        }

        @Override
        protected void doHandle(HttpServletRequest request) {
            String host = request.getHeader("Host");
            if (!allowedHosts.isEmpty() && (host == null || !allowedHosts.contains(host))) {
                RequestLogUtil.logRequestWarning(request, "不允许的 Host", "Host=" + host);
                throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "不允许的 Host");
            }
        }
    }

    /**
     * Content-Type 处理器
     */
    private static class ContentTypeHandler extends SecurityHandler {
        private final Set<String> blockedContentTypes;

        public ContentTypeHandler(Set<String> blockedContentTypes) {
            this.blockedContentTypes = blockedContentTypes;
        }

        @Override
        protected void doHandle(HttpServletRequest request) {
            String contentType = request.getContentType();
            if (contentType != null) {
                for (String blockedType : blockedContentTypes) {
                    if (contentType.contains(blockedType)) {
                        RequestLogUtil.logRequestWarning(request, "不允许的 Content-Type", "Content-Type=" + contentType);
                        throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "不允许的 Content-Type");
                    }
                }
            }
        }
    }

    /**
     * 请求大小处理器
     */
    private static class RequestSizeHandler extends SecurityHandler {
        private final long maxRequestSize;

        public RequestSizeHandler(long maxRequestSize) {
            this.maxRequestSize = maxRequestSize;
        }

        @Override
        protected void doHandle(HttpServletRequest request) {
            if (request.getContentLength() > maxRequestSize) {
                RequestLogUtil.logRequestWarning(request, "请求体过大", "Size=" + request.getContentLength());
                throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "请求体过大");
            }
        }
    }

    /**
     * 请求头大小处理器
     */
    private static class HeaderSizeHandler extends SecurityHandler {
        private final int maxHeaderSize;

        public HeaderSizeHandler(int maxHeaderSize) {
            this.maxHeaderSize = maxHeaderSize;
        }

        @Override
        protected void doHandle(HttpServletRequest request) {
            if (request.getHeader("Content-Length") != null) {
                try {
                    int headerSize = Integer.parseInt(request.getHeader("Content-Length"));
                    if (headerSize > maxHeaderSize) {
                        RequestLogUtil.logRequestWarning(request, "请求头过大", "Size=" + headerSize);
                        throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "请求头过大");
                    }
                } catch (NumberFormatException e) {
                    log.warn("无效的 Content-Length 头", e);
                }
            }
        }
    }

    /**
     * 参数处理器
     */
    private static class ParameterHandler extends SecurityHandler {
        private final int maxParameters;
        private final int maxParameterSize;

        public ParameterHandler(int maxParameters, int maxParameterSize) {
            this.maxParameters = maxParameters;
            this.maxParameterSize = maxParameterSize;
        }

        @Override
        protected void doHandle(HttpServletRequest request) {
            // 参数数量检查
            if (request.getParameterMap().size() > maxParameters) {
                RequestLogUtil.logRequestWarning(request, "参数数量过多", "Count=" + request.getParameterMap().size());
                throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "参数数量过多");
            }

            // 参数大小检查
            for (String[] values : request.getParameterMap().values()) {
                for (String value : values) {
                    if (value != null && value.length() > maxParameterSize) {
                        RequestLogUtil.logRequestWarning(request, "参数值过大", "Size=" + value.length());
                        throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "参数值过大");
                    }
                }
            }
        }
    }

    /**
     * HTTP 方法处理器
     */
    private static class HttpMethodHandler extends SecurityHandler {
        @Override
        protected void doHandle(HttpServletRequest request) {
            String method = request.getMethod();
            if (!method.equals("GET") && !method.equals("POST") && !method.equals("PUT") && !method.equals("DELETE")) {
                RequestLogUtil.logRequestWarning(request, "不支持的请求方法", "Method=" + method);
                throw new BusinessException(GlobalErrorCodeConstants.METHOD_NOT_ALLOWED.getType(), "不支持的请求方法");
            }
        }
    }
} 