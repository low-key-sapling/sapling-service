package com.sapling.framework.common.utils.enc;

/**
 * 私钥获取接口，供签名工具调用，由上层实现。
 */
public interface PrivateKeyProvider {
    /**
     * 获取SM2私钥
     * @return 私钥字符串
     */
    String getSm2PrivateKey();
} 