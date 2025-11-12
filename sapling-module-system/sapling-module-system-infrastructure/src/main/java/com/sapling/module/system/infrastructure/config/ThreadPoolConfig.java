package com.sapling.module.system.infrastructure.config;

import com.sapling.module.system.infrastructure.common.framework.properties.ThreadPoolProperties;
import com.sapling.module.system.infrastructure.common.framework.threadpool.MonitoredRejectedExecutionHandler;
import com.sapling.module.system.infrastructure.common.framework.threadpool.MonitoredThreadFactory;
import com.sapling.framework.core.context.decorator.RequestContextDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mbws
 * @version 1.0
 */
@Configuration
public class ThreadPoolConfig {

    @Resource
    private ThreadPoolProperties threadPoolProperties;

    @Resource
    private RequestContextDecorator requestContextDecorator;

    // 为每个线程池创建一个独立的计数器
    private final Map<String, AtomicInteger> threadCounters = new ConcurrentHashMap<>();
    // 记录每个线程池当前活跃的线程数
    private final Map<String, AtomicInteger> activeThreadCounters = new ConcurrentHashMap<>();

    /**
     * 获取活跃线程计数器Map
     * @return 活跃线程计数器Map
     */
    public Map<String, AtomicInteger> getActiveThreadCounters() {
        return activeThreadCounters;
    }


    @Bean("redisListMessageListenerExecutor")
    public ThreadPoolTaskExecutor redisListMessageListenerExecutor() {
        return createThreadPoolTaskExecutor("redisListMessageListenerExecutor", "RedisListMessageListenerExecutor-");
    }

    @Bean("queryAllOrgAndUserExecutor")
    public ThreadPoolTaskExecutor queryAllOrgAndUserExecutor() {
        return createThreadPoolTaskExecutor("queryAllOrgAndUserExecutor", "QueryAllOrgAndUserExecutor-");
    }

    /**
     * 创建线程池任务执行器
     *
     * @param executorName 线程池名称（对应配置文件中的key）
     * @param defaultThreadNamePrefix 默认线程名前缀
     * @return ThreadPoolTaskExecutor
     */
    private ThreadPoolTaskExecutor createThreadPoolTaskExecutor(String executorName, String defaultThreadNamePrefix) {
        ThreadPoolProperties.ThreadPoolConfig config = threadPoolProperties.getExecutorConfig(executorName);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        
        // 关键：根据队列容量决定是否使用同步队列
        if (config.getQueueCapacity() <= 0) {
            // 使用同步队列，不缓冲任务
            executor.setQueueCapacity(1);  // 设置为1，实际会使用SynchronousQueue
            executor.setAllowCoreThreadTimeOut(true);  // 允许核心线程超时，提高资源利用率
        } else {
            executor.setQueueCapacity(config.getQueueCapacity());
        }
        
        executor.setKeepAliveSeconds(config.getKeepAliveTime());
        executor.setThreadNamePrefix(config.getThreadNamePrefix() != null ? config.getThreadNamePrefix() : defaultThreadNamePrefix);
        executor.setAllowCoreThreadTimeOut(config.isAllowCoreThreadTimeOut());
        executor.setWaitForTasksToCompleteOnShutdown(config.isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(config.getAwaitTerminationSeconds());
        
        // 设置监控的线程工厂
        final String threadNamePrefix = executor.getThreadNamePrefix();
        executor.setThreadFactory(new MonitoredThreadFactory(r -> {
            Thread thread = new Thread(() -> {
                try {
                    // 增加活跃线程计数
                    AtomicInteger activeCounter = activeThreadCounters.computeIfAbsent(threadNamePrefix, k -> new AtomicInteger(0));
                    activeCounter.incrementAndGet();
                    
                    // 执行任务
                    r.run();
                } finally {
                    // 减少活跃线程计数
                    AtomicInteger activeCounter = activeThreadCounters.get(threadNamePrefix);
                    if (activeCounter != null) {
                        activeCounter.decrementAndGet();
                    }
                }
            });
            
            // 使用当前活动线程数作为线程名称
            AtomicInteger activeCounter = activeThreadCounters.computeIfAbsent(threadNamePrefix, k -> new AtomicInteger(0));
            thread.setName(threadNamePrefix + activeCounter.get());
            return thread;
        }));
        
        // 设置监控的拒绝策略
        RejectedExecutionHandler originalHandler = getRejectedExecutionHandler(config.getRejectedExecutionHandler());
        executor.setRejectedExecutionHandler(new MonitoredRejectedExecutionHandler(originalHandler));
        
        // 设置请求上下文装饰器
        executor.setTaskDecorator(requestContextDecorator);
        
        executor.initialize();
        return executor;
    }

    /**
     * 获取拒绝策略
     *
     * @param handlerName 拒绝策略名称
     * @return RejectedExecutionHandler
     */
    private RejectedExecutionHandler getRejectedExecutionHandler(String handlerName) {
        if (handlerName == null) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
        
        switch (handlerName.toUpperCase()) {
            case "ABORT":
                return new ThreadPoolExecutor.AbortPolicy();
            case "DISCARD":
                return new ThreadPoolExecutor.DiscardPolicy();
            case "DISCARD_OLDEST":
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            default:
                return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    }
}

