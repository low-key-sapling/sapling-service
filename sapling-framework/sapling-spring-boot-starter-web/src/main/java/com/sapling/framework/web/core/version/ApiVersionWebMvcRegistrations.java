package com.sapling.framework.web.core.version;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 实现WebMvcRegistrations接口，用于自定义WebMvc的注册逻辑
 * 主要用于API版本的请求映射配置
 *
 *  @author Artisan
 */
public class ApiVersionWebMvcRegistrations implements WebMvcRegistrations {

    /**
     * API版本配置属性
     * 用于获取API版本的前缀和后缀配置
     */
    private ApiVersionProperties apiVersionProperties;

    /**
     * 构造函数，初始化API版本配置属性
     *
     * @param apiVersionProperties API版本配置属性对象
     */
    public ApiVersionWebMvcRegistrations(ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    /**
     * 获取请求映射处理器映射对象
     * 此方法用于配置API版本的请求映射处理逻辑
     * 它根据配置决定映射路径的前缀和后缀
     *
     * @return 返回一个初始化好的RequestMappingHandlerMapping对象，用于处理API版本的请求映射
     */
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        // 根据API版本配置的前缀情况决定使用默认前缀"v"还是用户配置的前缀
        // 如果未配置前缀，则默认使用"v"，否则使用配置的前缀
        // 后缀直接使用配置的值
        return new ApiVersionRequestMappingHandlerMapping(StringUtils.isEmpty(apiVersionProperties.getPrefix()) ?
                "v" : apiVersionProperties.getPrefix(), apiVersionProperties.getSuffix());
    }

}
