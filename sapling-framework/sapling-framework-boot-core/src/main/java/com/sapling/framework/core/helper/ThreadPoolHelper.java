package com.sapling.framework.core.helper;

import com.sapling.framework.core.context.ioc.IOCContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Description: 线程池帮助类, TaskExecutionAutoConfiguration
 * @author Artisan
 */
public class ThreadPoolHelper {
    /**
     * 获取线程池
     *
     * @return ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        try {
            return IOCContext.getBean(ThreadPoolTaskExecutor.class);
        } catch (Exception exception) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setCorePoolSize(8);
            threadPoolTaskExecutor.setMaxPoolSize(64);
            threadPoolTaskExecutor.setQueueCapacity(10000);
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }
    }
}
