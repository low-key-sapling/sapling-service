package com.sapling.module.system.infrastructure.common.framework.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jiangwenzheng
 * @date 2023/6/14
 */
@Component
@Slf4j
public class ServletAuthenticationFilter extends OncePerRequestFilter {


    /**
     * 监控功能默认的关键字
     */
    private static final String KO_TIME = "koTime";


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getRequestURI().contains(KO_TIME)) {
            return true;
        }
        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);

    }


}
