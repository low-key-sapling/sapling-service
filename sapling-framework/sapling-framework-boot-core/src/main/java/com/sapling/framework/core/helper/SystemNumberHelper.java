package com.sapling.framework.core.helper;

import com.sapling.framework.core.context.ioc.IOCContext;
import com.sapling.framework.core.context.ContextProperties;

/**
 * @author artisan
 * @Description: 系统编号帮助类
 */
public class SystemNumberHelper {
    /**
     * 获取系统编号
     *
     * @return
     */
    public static String getSystemNumber() {
        return IOCContext.getBean(ContextProperties.class).getSystemNumber();
    }
}
