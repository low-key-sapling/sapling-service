package com.sapling.module.system.infrastructure.common.framework.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可监控的线程工厂
 * 用于统计线程池任务执行时间
 */
@Slf4j
public class MonitoredThreadFactory implements ThreadFactory {

    private final ThreadFactory delegate;
    private final Map<String, AtomicLong> totalExecutionTimeMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalTaskCountMap = new ConcurrentHashMap<>();

    public MonitoredThreadFactory(ThreadFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Thread newThread(Runnable r) {
        return delegate.newThread(new MonitoredRunnable(r, this));
    }

    /**
     * 记录任务执行时间
     *
     * @param poolName 线程池名称
     * @param executionTime 执行时间（毫秒）
     */
    void recordExecutionTime(String poolName, long executionTime) {
        totalExecutionTimeMap.computeIfAbsent(poolName, k -> new AtomicLong(0)).addAndGet(executionTime);
        totalTaskCountMap.computeIfAbsent(poolName, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * 获取线程池平均任务执行时间
     *
     * @param executor 线程池执行器
     * @return 平均执行时间（毫秒）
     */
    public long getAverageExecutionTime(ThreadPoolTaskExecutor executor) {
        String poolName = executor.getThreadNamePrefix();
        AtomicLong totalTime = totalExecutionTimeMap.getOrDefault(poolName, new AtomicLong(0));
        AtomicLong totalCount = totalTaskCountMap.getOrDefault(poolName, new AtomicLong(0));
        
        if (totalCount.get() == 0) {
            return 0;
        }
        
        return totalTime.get() / totalCount.get();
    }

    /**
     * 可监控的Runnable包装器
     */
    private static class MonitoredRunnable implements Runnable {
        private final Runnable delegate;
        private final MonitoredThreadFactory factory;
        private final String poolName;

        public MonitoredRunnable(Runnable delegate, MonitoredThreadFactory factory) {
            this.delegate = delegate;
            this.factory = factory;
            this.poolName = Thread.currentThread().getName();
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                delegate.run();
            } finally {
                long executionTime = System.currentTimeMillis() - startTime;
                factory.recordExecutionTime(poolName, executionTime);
            }
        }
    }
} 