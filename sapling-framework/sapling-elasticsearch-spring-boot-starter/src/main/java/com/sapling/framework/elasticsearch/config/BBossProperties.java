package com.sapling.framework.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */

@ConfigurationProperties(prefix = BBossProperties.PREFIX)
public class BBossProperties {

    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "sapling.es.bboss";

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
    