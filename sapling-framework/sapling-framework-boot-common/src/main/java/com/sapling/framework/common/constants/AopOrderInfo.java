package com.sapling.framework.common.constants;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: 定义优先级顺序
 * @mark: show me the code , change the world
 */
public class AopOrderInfo {
    /**
     * API请求切面
     */
    public static final int REQUEST = 400;
    /**
     * API请求拦截器
     */
    public static final int REQUEST_INTERCEPTOR = 410;
    /**
     * Mybatis日志切面
     */
    public static final int MYBATIS = 850;
    /**
     * MYBATIS拦截器
     */
    public static final int MYBATIS_INTERCEPTOR = 852;
    /**
     * 数据源切面
     */
    public static final int DATASOURCE = 900;
    /**
     * 数据库AOP切面拦截器
     */
    public static final int DATASOURCE_INTERCEPTOR = 910;
    /**
     * RestTemplate请求拦截器优先级
     */
    public static final int HTTP_CLIENT_INTERCEPTOR = 1000;


}
