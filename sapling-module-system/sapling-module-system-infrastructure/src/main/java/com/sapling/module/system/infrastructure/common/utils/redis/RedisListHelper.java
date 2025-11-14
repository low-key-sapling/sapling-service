package com.sapling.module.system.infrastructure.common.utils.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: List类型相关操作
 * @mark: show me the code , change the world
 */
@Component
public class RedisListHelper<T> extends RedisCommonHelper {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    /**
     * 根据 索引获取 list缓存值
     *
     * @param key   键
     * @param start 开始索引（下标从0开始）
     * @param end   偏移量（-1，则遍历全部数据）
     * @return 指定索引范围内的元素
     */
    public List<T> range(String key, long start, long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取 List缓存的长度
     *
     * @param key 键
     * @return
     */
    public long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取 key键 对应集合中 index索引的值
     *
     * @param key   键
     * @param index 索引
     * @return key键 对应集合中 index索引的值
     */
    public T index(String key, long index) {
        return (T) redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将 value值放入 key对应的List集合 尾部
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }


    /**
     * 为已存在的列表添加值
     *
     * @param key   存在的列表
     * @param value 值
     * @return 列表长度
     */
    public Long lRightPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }


    /**
     * 将 value值数组放入 key对应的List集合 尾部
     *
     * @param key    键
     * @param values 值数组
     * @return 列表长度
     */
    public Long rightPush(String key, T... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 将多个值存入列表中
     *
     * @param key   列表
     * @param value 值的集合
     * @return 列表长度
     */
    public Long lRightPushAll(String key, Collection<Object> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 将 value值放入 key对应的List集合 头部
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long leftPush(String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将 value值数组放入 key对应的List集合 头部
     *
     * @param key    键
     * @param values 值数组
     * @return 列表长度
     */
    public Long leftPush(String key, T... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 将多个值存入列表中
     *
     * @param key   列表
     * @param value 值的集合
     * @return 列表长度
     */
    public Long lLeftPushAll(String key, Collection<T> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 当 list 存在的时候才加入
     *
     * @param key   列表
     * @param value 值
     * @return 列表长度
     */
    public Long lLeftPushIfPresent(String key, T value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 修改 key键对应的List集合中 索引为index的值
     *
     * @param key   键
     * @param index 索引
     * @param value 要更改的值
     */
    public void setIndex(String key, long index, T value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 裁剪 list
     *
     * @param key   列表
     * @param start 起始位置
     * @param end   结束位置
     * @see <a href="https://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
     */
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }


    /**
     * 移出并获取列表的第一个元素
     *
     * @param key 列表
     * @return 删除的元素
     */
    public T lLeftPop(String key) {
        return (T)redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素, 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     列表
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return 删除的元素
     */
    public T lBLeftPop(String key, long timeout, TimeUnit unit) {
        return (T)redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 删除集合中值等于 value 的元素
     *
     * @param key   列表
     * @param index index = 0, 删除所有值等于value的元素;
     *              index > 0, 从头部开始删除第一个值等于 value 的元素;
     *              index < 0, 从尾部开始删除第一个值等于 value 的元素;
     * @param value 值
     * @return 列表长度
     */
    public Long lRemove(String key, long index, T value) {
        return redisTemplate.opsForList().remove(key, index, value);
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @param key 列表
     * @return 删除的元素
     */
    public T lRightPop(String key) {
        return (T)redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素, 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     列表
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return 删除的元素
     */
    public T lBRightPop(String key, long timeout, TimeUnit unit) {
        return (T)redisTemplate.opsForList().rightPop(key, timeout, unit);
    }



    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     *
     * @param sourceKey      要移除元素的列表
     * @param destinationKey 要添加元素的列表
     * @return 移动的元素
     */
    public T lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return (T)redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它; 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey      要移除元素的列表
     * @param destinationKey 要添加元素的列表
     * @param timeout        等待时间
     * @param unit           时间单位
     * @return 移动的元素
     */
    public T lBRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return (T)redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
    }
}
    