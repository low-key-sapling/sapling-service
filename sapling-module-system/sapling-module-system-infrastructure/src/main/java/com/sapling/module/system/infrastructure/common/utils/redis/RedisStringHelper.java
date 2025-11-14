package com.sapling.module.system.infrastructure.common.utils.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: Redis String操作工具类
 * @mark: show me the code , change the world
 */

@Component
public class RedisStringHelper<T> extends RedisCommonHelper {
    
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    
    
    /**
     * 获取key键对应的值（GET key）
     *
     * @param key 键
     * @return
     */
    public T get(String key) {
        if (null == key) {
            return null;
        }
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 新增一个字符串类型的值,key是键，value是值（SET key value）
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new RuntimeException("放入缓存异常：", e);
        }
    }

    /**
     * 放入缓存并设置失效时间（setex key seconds value）
     *
     * @param key     键
     * @param value   值
     * @param timeout 失效时间（单位：秒，小于等于0 表示 永久有效）
     */
    public void set(String key, T value, long timeout) {
        set(key, value);
        expire(key, timeout);
    }

    /**
     * 在原有的值基础上新增字符串到末尾（append key value）
     *
     * @param key   键
     * @param value 新增的字符串
     */
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 截取key键对应值得字符串，从开始下标位置到结束下标位置(包含)的字符串（getrange key start end）
     *
     * @param key   键
     * @param start 开始下标位置
     * @param end   结束下标的位置
     * @return 从开始下标位置到结束下标位置(包含)的字符串
     */
    public String get(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 获取原来key键对应的值并重新赋新值（getset key value）
     *
     * @param key   键
     * @param value 新值
     * @return 旧值（原来key键对应的值）
     */
    public T getAndSet(String key, T value) {
        return (T)redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 获取指定key键对应值的长度（strlen key）
     *
     * @param key 键
     * @return 返回对应值的长度
     */
    public long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 将存储在key键上的数字按指定的值 增加（incrby key number）
     * 增加(自增长), 负数则为自减
     *
     * @param key   键
     * @param delta 指定的增加数字
     * @return 返回增加后的值（key键不存在，则在执行操作之前将其设置为0）
     */
    public long incrby(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自增长, 增长量为浮点数
     *
     * @param key   键
     * @param delta 自增量
     * @return 增加后的值
     */
    public Double incrByFloat(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将存储在key键上的数字按指定的值 减少（incrby key number）
     *
     * @param key   键
     * @param delta 指定的减少数字
     * @return 返回减少后的值（key键不存在，则在执行操作之前将其设置为0）
     */
    public long decrby(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 如果键不存在则新增,存在则不改变已经有的值（setnx key value）
     *
     * @param key   键
     * @param value 值
     * @return key键不存在，返回ture；存在返回false
     */
    public boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 一次多个键设置它们的值（mset key1 value1 key2 value2 ..）
     *
     * @param keyValueMap key为键，value为值
     */
    public void multiSet(Map<String, Object> keyValueMap) {
        redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在才会设置成功
     *
     * @param maps key-value 集合
     * @return 之前已经存在返回 false, 不存在返回 true
     */
    public Boolean multiSetIfAbsent(Map<String, Object> maps) {
        return redisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 根据键数组取出对应的value值（mget key1 key2 ..）
     *
     * @param keys 键数组
     * @return
     */
    public List<?> multiGet(String... keys) {
        return redisTemplate.opsForValue().multiGet((Collection<String>) CollectionUtils.arrayToList(keys));
    }


    /**
     * 返回 key 的 字符串值 中指定位置的 子字符串
     *
     * @param key   要获取值的 键
     * @param start 开始位置, 最小值: 0
     * @param end   结束位置, 最大值: 字符串 - 1, 若为 -1 则是获取整个字符串值
     * @return 指定 键 的 字符串值 的 子字符串
     */
    public String getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位( bit )
     *
     * @param key    键
     * @param offset 偏移量
     * @return 指定偏移量上的 位( 0 / 1)
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第 offset 位值变为 value
     *
     * @param key    要设置的 键
     * @param offset 偏移多少位
     * @param value  值, true 为 1,  false 为 0
     * @return 设置成功返回 true, 设置失败返回 false
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位, 天: TimeUnit.DAYS 小时: TimeUnit.HOURS 分钟: TimeUnit.MINUTES
     *                秒: TimeUnit.SECONDS 毫秒: TimeUnit.MILLISECONDS
     */
    public void setEx(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     *
     * @param key    键
     * @param value  值
     * @param offset 从指定位置开始覆写
     */
    public void setRange(String key, Object value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }


}
    