package com.sapling.module.system.infrastructure.common.framework.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可监控的拒绝策略处理器
 * 用于统计线程池拒绝的任务数
 */
@Slf4j
public class MonitoredRejectedExecutionHandler implements RejectedExecutionHandler {

    private final RejectedExecutionHandler delegate;
    private final Map<String, AtomicLong> rejectedCountMap = new ConcurrentHashMap<>();

    public MonitoredRejectedExecutionHandler(RejectedExecutionHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        String poolName = getPoolName(executor);
        rejectedCountMap.computeIfAbsent(poolName, k -> new AtomicLong(0)).incrementAndGet();
        log.warn("线程池[{}]拒绝执行任务", poolName);
        delegate.rejectedExecution(r, executor);
    }

    /**
     * 获取线程池拒绝的任务数
     *
     * @param executor 线程池执行器
     * @return 拒绝的任务数
     */
    public long getRejectedCount(ThreadPoolTaskExecutor executor) {
        String poolName = executor.getThreadNamePrefix();
        return rejectedCountMap.getOrDefault(poolName, new AtomicLong(0)).get();
    }

    /**
     * 获取线程池名称
     *
     * @param executor 线程池执行器
     * @return 线程池名称
     */
    private String getPoolName(ThreadPoolExecutor executor) {
        return executor.toString();
    }
} 