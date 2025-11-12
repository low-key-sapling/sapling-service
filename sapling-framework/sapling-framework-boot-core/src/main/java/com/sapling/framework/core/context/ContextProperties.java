package com.sapling.framework.core.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @Description: 链路追踪上下文属性配置
 * @author Artisan
 */
@ConfigurationProperties(prefix = ContextProperties.PREFIX)
public class ContextProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "zf.base.context";
    /**
     * 系统编号
     */
    private String systemNumber;

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }
}
