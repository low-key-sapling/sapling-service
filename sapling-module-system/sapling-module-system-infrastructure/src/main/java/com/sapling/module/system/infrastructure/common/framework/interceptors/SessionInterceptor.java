package com.sapling.module.system.infrastructure.common.framework.interceptors;

import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.client.components.rc.ItxCoreRequestClient;
import com.sapling.module.system.domain.biz.system.gateway.SysParamGateWay;
import com.sapling.module.system.infrastructure.common.framework.annos.RequiresPermission;
import com.sapling.module.system.infrastructure.common.utils.ParamThreadLocal;
import com.sapling.framework.common.exception.BusinessException;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Session拦截器
 * 用于处理需要Session验证的请求
 */
@Slf4j
@Component
public class SessionInterceptor implements HandlerInterceptor {
    /**
     * sessionId标识
     */
    public static final String SESSION_ID = "session_id";
    /**
     * session id 失效后平台的返回信息包含的字段
     */
    public static final String OFF_LINE = "尚未登录";

    @Resource
    private ItxCoreRequestClient itxCoreRequestClient;

    @Resource
    private SysParamGateWay sysParamGateWay;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws IOException {
        // 获取session_id 和 zmct
        ParamThreadLocal.set(request.getCookies());
        Cookie[] cookies = (Cookie[]) ParamThreadLocal.getAndRemove();
        String sessionId = getCookie(cookies, SESSION_ID);
        String tokenRequest = request.getHeader("zmct");

        // 判断sessionId是否为空
        if (StringUtils.isEmpty(sessionId) && StringUtils.isEmpty(tokenRequest)) {
            // 如果sessionId为空 返回错误信息
            log.error("url:{} 拦截校验失败：session_id 或 zmct不能为空", request.getRequestURI());
            throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "session_id 或 zmct不能为空");
        }

        // 验证sessionId
        String zmct = sysParamGateWay.getZmct();
        String account = itxCoreRequestClient.currentLoginInfo(sessionId, zmct);
        // 账号为空或者尚未登录，校验token
        if (StringUtil.isNullOrEmpty(account) || account.contains(OFF_LINE)) {
            log.info("url:{} session: {} account: {}", request.getRequestURI(), sessionId, account);
            // 没有用户信息，校验头参是否存在zmct
            if (StringUtils.isNotEmpty(tokenRequest)) {
                if (StringUtils.isNotEmpty(zmct) && !zmct.equals(tokenRequest)) {
                    log.info("{} 校验头参zmct失败", request.getRequestURI());
                    throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "头信息错误！");
                }
            } else {
                throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED.getType(), "请求校验失败！");
            }
        }
        if (StringUtils.isNotEmpty(sessionId)) {
            // 校验菜单权限，决定是否接口放行
            JSONObject userInfo = JSONObject.parseObject(account);
            if (!isHavePermission(handler, userInfo.getString("permission_code"))) {
                log.info("url:{} 菜单权限校验失败,用户无权限", request.getRequestURI());
                throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN.getType(), "菜单权限校验失败,用户无操作权限！");
            }
            // 如果验证成功调用平台接口保活sessionId 并放行请求
            itxCoreRequestClient.keepSession(sessionId, zmct);
        }
        return true;
    }

    public static String getCookie(Cookie[] cookies, String name) {
        Cookie cookie = null;
        if (cookies != null) {
            for (Cookie ck : cookies) {
                if (ck.getName().equals(name)) {
                    cookie = ck;
                }
            }
        }
        return cookie != null ? cookie.getValue() : "";
    }

    /**
     * 用户菜单权限校验
     *
     * @param handler        处理
     * @param userPermission 平台用户权限
     * @return true|false 是否拥有权限
     */
    public boolean isHavePermission(Object handler, String userPermission) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 检查方法上的注解
            RequiresPermission permission = method.getAnnotation(RequiresPermission.class);
            if (permission == null) {
                // 检查类上面的注解
                permission = ((HandlerMethod) handler).getBeanType().getAnnotation(RequiresPermission.class);
            }
            if (permission != null) {
                String[] requiredPermissions = permission.value();
                List<String> userPerMissionList = Arrays.asList(userPermission.split(","));
                // 校验权限
                return Arrays.stream(requiredPermissions).anyMatch(userPerMissionList::contains);
            }
        }
        return true;
    }
}