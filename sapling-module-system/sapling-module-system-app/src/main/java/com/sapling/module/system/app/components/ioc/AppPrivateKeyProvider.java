package com.sapling.module.system.app.components.ioc;

import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.common.utils.enc.PrivateKeyProvider;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * App 层私钥提供者实现
 */
@Slf4j
@Component
public class AppPrivateKeyProvider implements PrivateKeyProvider {
    
    private static final AtomicReference<String> SM2_PRIVATE_KEY = new AtomicReference<>();
    
    @Override
    public String getSm2PrivateKey() {
        String privateKey = SM2_PRIVATE_KEY.get();
        if (privateKey == null) {
            log.warn("SM2私钥未初始化");
            return null;
        }
        log.debug("获取SM2私钥成功");
        return privateKey;
    }
    
    /**
     * 设置 SM2 私钥
     * @param privateKey SM2 私钥
     */
    public void setSm2PrivateKey(String privateKey) {
        SM2_PRIVATE_KEY.set(privateKey);
        log.info("SM2私钥设置成功");
    }
} 