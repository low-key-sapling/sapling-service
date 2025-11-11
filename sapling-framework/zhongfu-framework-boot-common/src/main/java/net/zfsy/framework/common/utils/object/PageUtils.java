package net.zfsy.framework.common.utils.object;

import net.zfsy.framework.common.pojo.PageParam;

/**
 * {@link net.zfsy.framework.common.pojo.PageParam} 工具类
 *
 * @author 端点安全
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPage() - 1) * pageParam.getPageSize();
    }

}
