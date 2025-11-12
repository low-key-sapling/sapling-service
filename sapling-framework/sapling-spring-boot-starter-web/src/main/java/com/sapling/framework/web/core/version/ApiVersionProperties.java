package com.sapling.framework.web.core.version;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置属性类用于管理API版本。
 * 通过前缀 "api-version" 绑定配置属性，以方便管理API版本。
 * @author Artisan
 */

@ConfigurationProperties(prefix = "api-version")
public class ApiVersionProperties {

    /**
     * API版本的前缀，用于定义版本的起始部分。
     */
    private String prefix;

    /**
     * 获取API版本的前缀。
     *
     * @return 返回API版本的前缀。
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置API版本的前缀。
     *
     * @param prefix 设置API版本的前缀。
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * API版本的后缀，用于定义版本的结束部分。
     */
    private String suffix;

    /**
     * 获取API版本的后缀。
     *
     * @return 返回API版本的后缀。
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 设置API版本的后缀。
     *
     * @param suffix 设置API版本的后缀。
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
