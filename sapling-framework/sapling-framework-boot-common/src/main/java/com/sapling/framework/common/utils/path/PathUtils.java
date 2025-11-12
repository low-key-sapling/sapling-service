package com.sapling.framework.common.utils.path;

import org.springframework.util.StringUtils;

/**
 * @author Artisan
 * @Description: 路径工具类
 */
public class PathUtils {
    /**
     * @param path 路径
     * @return
     * @Description 规范化路径
     */
    public static String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }
        String normalizedPath = path;
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }
}
