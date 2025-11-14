package com.sapling.module.system.infrastructure.common.framework.interceptors;

import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.framework.security.RuleEngine;
import com.sapling.module.system.infrastructure.common.framework.security.RuleMatchResult;
import com.sapling.module.system.infrastructure.common.utils.log.RequestLogUtil;
import com.sapling.framework.common.exception.BusinessException;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 安全拦截器：规则引擎检查
 */
@Slf4j
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Value("${endpoint.security.enabled:true}")
    private boolean enabled;

    @Autowired
    private RuleEngine ruleEngine;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enabled) {
            return true;
        }

        // 包装请求以支持多次读取请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        // 记录请求信息
        log.debug("收到请求: URI={}, Method={}, Content-Type={}",
            request.getRequestURI(),
            request.getMethod(),
            request.getContentType());

        // 规则引擎检查
        Optional<RuleMatchResult> result = ruleEngine.check(requestWrapper);
        if (result.isPresent()) {
            RuleMatchResult matchResult = result.get();
            RequestLogUtil.logRequestWarning(request, "安全规则匹配", "规则=" + matchResult.getRuleName());
            throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN.getType(),
                "请求被拒绝",
                "检测到潜在的安全风险：" + matchResult.getDescription());
        }

        return true;
    }
} 