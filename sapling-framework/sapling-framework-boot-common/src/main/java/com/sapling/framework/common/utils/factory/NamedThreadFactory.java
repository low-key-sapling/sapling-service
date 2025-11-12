package com.sapling.framework.common.utils.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名线程工厂
 * 用于创建具有指定名称前缀的线程
 *
 * @author mbws
 */
public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final ThreadGroup group;

    /**
     * 创建一个命名线程工厂
     *
     * @param namePrefix 线程名称前缀
     */
    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, true);
    }

    /**
     * 创建一个命名线程工厂
     *
     * @param namePrefix 线程名称前缀
     * @param daemon 是否为守护线程
     */
    public NamedThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "-" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
} 