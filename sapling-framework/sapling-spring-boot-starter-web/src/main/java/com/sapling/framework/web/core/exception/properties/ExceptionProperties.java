package com.sapling.framework.web.core.exception.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author artisan
 * @Description: 异常处理自动化配置PO
 */
@ConfigurationProperties(prefix = ExceptionProperties.PREFIX)
public class ExceptionProperties {
    /**
     * 配置前缀
     */
    public static final String PREFIX = "zf.global.exception";
    /**
     * 是否开启异常拦截
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
