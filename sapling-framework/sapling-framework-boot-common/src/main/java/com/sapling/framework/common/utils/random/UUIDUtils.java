package com.sapling.framework.common.utils.random;

import com.sapling.framework.common.constants.CharacterInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author 小工匠
 * @Description: 自动生成随机字符串工具类
 * @version 1.0
 */
public class UUIDUtils {
    /**
     * 自动生成用户令牌
     */
    public static String generation() {
        return StringUtils.replace(randomUuid(), CharacterInfo.LINE_THROUGH_CENTER, "");
    }

    /**
     * 生成唯一标识
     *
     * @return
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }
}
