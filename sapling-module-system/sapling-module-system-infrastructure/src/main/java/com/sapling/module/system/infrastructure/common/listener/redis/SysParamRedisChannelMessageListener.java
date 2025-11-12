package com.sapling.module.system.infrastructure.common.listener.redis;

import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.constants.RedisKeyConstants;
import com.sapling.module.system.infrastructure.gatewayImpl.SysParamGateWayImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统参数Redis通道消息监听器
 * 用于监听系统参数变更通知，并清除本地缓存
 */
@Slf4j
@Component
public class SysParamRedisChannelMessageListener extends AbstractRedisChannelMessageListener<String> {

    @Resource
    private SysParamGateWayImpl sysParamGateWayImpl;

    @Override
    public String getChannel() {
        return RedisKeyConstants.ItxCacheKey.SYS_PARAM_REFRESH_CHANNEL;
    }

    @Override
    public void handle(String paramId) {
        try {
            log.info("收到系统参数变更通知，参数ID：{}", paramId);
            // 清除本地缓存
            sysParamGateWayImpl.invalidateLocalCache(paramId);
        } catch (Exception e) {
            log.error("处理系统参数变更通知失败", e);
        }
    }
} 