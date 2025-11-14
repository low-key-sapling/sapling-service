package com.sapling.module.system.infrastructure.common.utils.redis;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class RedisBloomFilterHelper<T> {
    @Resource
    private RedissonClient redissonClient;

    // 存储所有过滤器
    private final Map<String, RBloomFilter<T>> FILTER_MAP = new ConcurrentHashMap<>(10);
    // 内存锁
    private final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 创建布隆过滤器
     *
     * @param name               过滤器名称
     * @param expectedInsertions 初始化元素数量
     * @param fpp                误判率
     * @return 布隆过滤器
     */
    public RBloomFilter<T> initFilter(String name, long expectedInsertions, double fpp) {
        LOCK.lock();
        try {
            // 如果已经初始化了，则返回
            if (FILTER_MAP.containsKey(name)) {
                return FILTER_MAP.get(name);
            }

            // 创建布隆过滤器
            RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(name);
            if (!bloomFilter.isExists()) {
                bloomFilter.tryInit(expectedInsertions, fpp);
            }
            FILTER_MAP.put(name, bloomFilter);
            return bloomFilter;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 添加元素
     *
     * @param name  过滤器名称
     * @param value 值
     * @return 是否添加成功
     */
    public boolean add(String name, T value) {
        RBloomFilter<T> bloomFilter = FILTER_MAP.get(name);
        if (bloomFilter == null) {
            return false;
        }
        return bloomFilter.add(value);
    }

    /**
     * 判断元素是否存在
     *
     * @param name  过滤器名称
     * @param value 值
     * @return 是否存在
     */
    public boolean contains(String name, T value) {
        RBloomFilter<T> bloomFilter = FILTER_MAP.get(name);
        if (bloomFilter == null) {
            return false;
        }
        return bloomFilter.contains(value);
    }
}
