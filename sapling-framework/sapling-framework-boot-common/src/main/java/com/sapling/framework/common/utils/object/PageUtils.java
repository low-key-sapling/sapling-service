package com.sapling.framework.common.utils.object;

import com.sapling.framework.common.pojo.PageParam;

/**
 * {@link PageParam} 工具类
 *
 * @author 端点安全
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPage() - 1) * pageParam.getPageSize();
    }

}
