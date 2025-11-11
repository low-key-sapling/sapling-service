package net.zfsy.framework.core.helper;

import net.zfsy.framework.core.context.ContextProperties;
import net.zfsy.framework.core.context.ioc.IOCContext;

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
