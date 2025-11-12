package com.sapling.framework.core.servlet;

import com.sapling.framework.common.exception.enums.AppHttpStatus;
import com.sapling.framework.common.exception.BasicException;
import com.sapling.framework.common.exception.PrintExceptionInfo;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Artisan
 * @Description: 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link DelegateRequestWrapper}
 */
public class RequestChannelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        return;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            ServletRequest requestWrapper;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new DelegateRequestWrapper((HttpServletRequest) request);
                chain.doFilter(requestWrapper, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (IOException ex) {
            throw new BasicException(AppHttpStatus.IO_EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        } catch (ServletException ex) {
            throw new BasicException(AppHttpStatus.EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        }


    }

    @Override
    public void destroy() {
        return;
    }

}
