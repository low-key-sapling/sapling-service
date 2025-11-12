package com.sapling.module.system.infrastructure.common.listener.redis;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.constants.RedisChannelConstants;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegInfoRedisChannelMessageListener<RegInfo> extends AbstractRedisChannelMessageListener<RegInfo> {
    @Override
    public String getChannel() {
        return RedisChannelConstants.R_KEY_REG_INFO;
    }

    @Override
    public void handle(RegInfo regInfo) {
        log.info("收到消息：" + JSONUtil.toJsonStr(regInfo));
    }
}
