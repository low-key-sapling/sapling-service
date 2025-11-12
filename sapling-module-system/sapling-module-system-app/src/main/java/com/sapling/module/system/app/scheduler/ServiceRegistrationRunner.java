package com.sapling.module.system.app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.app.components.ioc.AppPrivateKeyProvider;
import com.sapling.framework.common.utils.enc.Sm2SignUtil;
import com.sapling.framework.common.utils.enc.Sm2Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务注册初始化运行器
 * 负责系统启动时的密钥生成和服务注册初始化
 */
@Slf4j
@Component
public class ServiceRegistrationRunner implements ApplicationRunner {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private AppPrivateKeyProvider privateKeyProvider;

    @Override
    public void run(ApplicationArguments args) {
        log.info("系统启动-开始初始化服务注册");
        try {
            // 注入私钥提供者到签名工具
            Sm2SignUtil.setPrivateKeyProvider(privateKeyProvider);
            // 使用模板方法执行初始化流程
            new ServiceRegistrationTemplate()
                    .setKeyPairGenerator(new Sm2KeyPairGenerator())
                    .setMetadataPreparer(new DefaultMetadataPreparer())
                    .setPrivateKeyProvider(privateKeyProvider)
                    .execute();
            log.info("系统启动-服务注册初始化完成");
        } catch (Exception e) {
            log.error("系统启动-服务注册初始化失败:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 服务注册模板类
     * 定义服务注册的流程
     */
    private static class ServiceRegistrationTemplate {
        private KeyPairGenerator keyPairGenerator;
        private MetadataPreparer metadataPreparer;
        private AppPrivateKeyProvider privateKeyProvider;

        public ServiceRegistrationTemplate setKeyPairGenerator(KeyPairGenerator keyPairGenerator) {
            this.keyPairGenerator = keyPairGenerator;
            return this;
        }

        public ServiceRegistrationTemplate setMetadataPreparer(MetadataPreparer metadataPreparer) {
            this.metadataPreparer = metadataPreparer;
            return this;
        }

        public ServiceRegistrationTemplate setPrivateKeyProvider(AppPrivateKeyProvider privateKeyProvider) {
            this.privateKeyProvider = privateKeyProvider;
            return this;
        }


        public void execute() {
            // 1. 生成密钥对
            Map<String, String> keyPair = keyPairGenerator.generate();
            if (!validateKeyPair(keyPair)) {
                log.error("系统启动-生成密钥对失败或密钥对无效");
                return;
            }
            // 存储私钥
            privateKeyProvider.setSm2PrivateKey(keyPair.get(Sm2Utils.PRIVATE_KEY));
            log.info("系统启动-生成密钥对成功");

            // 2. 准备元数据
            Map<String, Object> metadata = metadataPreparer.prepare(keyPair);
            if (metadata == null) {
                log.error("系统启动-准备元数据失败");
                return;
            }
            log.info("系统启动-准备元数据成功: {}", metadata);
        }

        private boolean validateKeyPair(Map<String, String> keyPair) {
            if (keyPair == null) {
                return false;
            }
            String publicKey = keyPair.get(Sm2Utils.PUBLIC_KEY);
            String privateKey = keyPair.get(Sm2Utils.PRIVATE_KEY);
            return StringUtils.isNotBlank(publicKey) && StringUtils.isNotBlank(privateKey);
        }
    }

    /**
     * 密钥对生成器接口
     */
    private interface KeyPairGenerator {
        Map<String, String> generate();
    }

    /**
     * SM2密钥对生成器
     */
    private static class Sm2KeyPairGenerator implements KeyPairGenerator {
        @Override
        public Map<String, String> generate() {
            try {
                Map<String, String> keyPair = Sm2Utils.generateKeyPairBase64();
                if (keyPair == null) {
                    log.error("生成SM2密钥对失败: 返回值为null");
                    return null;
                }
                return keyPair;
            } catch (Exception e) {
                log.error("生成SM2密钥对失败:{}", ExceptionUtils.getStackTrace(e));
                return null;
            }
        }
    }

    /**
     * 元数据准备器接口
     */
    private interface MetadataPreparer {
        Map<String, Object> prepare(Map<String, String> keyPair);
    }

    /**
     * 默认元数据准备器
     */
    private static class DefaultMetadataPreparer implements MetadataPreparer {
        @Override
        public Map<String, Object> prepare(Map<String, String> keyPair) {
            try {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("publicKey", keyPair.get(Sm2Utils.PUBLIC_KEY));
                return metadata;
            } catch (Exception e) {
                log.error("准备元数据失败:{}", ExceptionUtils.getStackTrace(e));
                return null;
            }
        }
    }
} 