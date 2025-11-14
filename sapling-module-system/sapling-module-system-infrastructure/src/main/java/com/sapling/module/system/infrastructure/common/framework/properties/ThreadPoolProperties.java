package com.sapling.module.system.infrastructure.common.framework.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程池配置属性类
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {

    /**
     * 线程池配置映射
     * key: 线程池名称
     * value: 线程池配置
     */
    private Map<String, ThreadPoolConfig> executors = new HashMap<>();

    /**
     * 线程池通用配置
     */
    @Data
    public static class ThreadPoolConfig {
        /**
         * 核心线程数
         */
        private int corePoolSize = 32;

        /**
         * 最大线程数
         */
        private int maxPoolSize = 64;

        /**
         * 允许线程空闲时间（单位：秒）
         */
        private int keepAliveTime = 10;

        /**
         * 缓冲队列大小
         */
        private int queueCapacity = 16;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix;

        /**
         * 是否允许核心线程超时
         */
        private boolean allowCoreThreadTimeOut = true;

        /**
         * 等待所有任务完成后再关闭线程池
         */
        private boolean waitForTasksToCompleteOnShutdown = true;

        /**
         * 等待时间（单位：秒）
         */
        private int awaitTerminationSeconds = 60;

        /**
         * 拒绝策略
         * 可选值：ABORT, DISCARD, DISCARD_OLDEST, CALLER_RUNS
         */
        private String rejectedExecutionHandler = "CALLER_RUNS";
    }

    /**
     * 获取指定线程池的配置
     *
     * @param executorName 线程池名称
     * @return 线程池配置
     */
    public ThreadPoolConfig getExecutorConfig(String executorName) {
        ThreadPoolConfig config = executors.getOrDefault(executorName, new ThreadPoolConfig());
        log.info("获取线程池[{}]配置: corePoolSize={}, maxPoolSize={}, queueCapacity={}", 
            executorName, config.getCorePoolSize(), config.getMaxPoolSize(), config.getQueueCapacity());
        return config;
    }

    @PostConstruct
    public void init() {
        log.info("线程池配置加载完成，当前配置的线程池列表：");
        executors.forEach((name, config) -> {
            log.info("线程池[{}]配置: corePoolSize={}, maxPoolSize={}, queueCapacity={}", 
                name, config.getCorePoolSize(), config.getMaxPoolSize(), config.getQueueCapacity());
        });
    }
} 