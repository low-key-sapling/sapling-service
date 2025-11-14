package com.sapling.module.system.infrastructure.common.utils.redis;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: Hash类型相关操作
 * @mark: show me the code , change the world
 */

@Component
public class RedisHashHelper<HK, HV> extends RedisCommonHelper {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 将hash表中放入数据,如果不存在将创建（hset key item value）
     *
     * @param key   redis key
     * @param item  hash key
     * @param value hash value
     */
    public void hset(String key, HK item, HV value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 将hash表中放入数据,如果不存在将创建，设置失效时间（hset key item value；expire key timeout）
     * <pre>
     *     失效时间说明：
     *      1、设置的失效时间是针对整个 redis key，并非是只能 当前的 hash key；
     *      2、如果已存在的 hash表有时间，这里将会替换原有的时间
     * </pre>
     *
     * @param key     redis key
     * @param item    hash key
     * @param value   hash value
     * @param timeout 失效时间（单位：秒，小于等于0 表示 永久有效）
     */
    public void hset(String key, HK item, HV value, long timeout) {
        redisTemplate.opsForHash().put(key, item, value);

        // 设置 key 失效时间
        expire(key, timeout);
    }

    /**
     * 将 map 表中放入数据,如果不存在将创建（hmset key item1 value1 item2 value2 item3 value3 ...）
     *
     * @param key  redis key
     * @param hash map 表中放入数据
     */
    public void hmSet(String key, Map<HK, HV> hash) {
        redisTemplate.opsForHash().putAll(key, hash);
    }

    /**
     * 将 map 表中放入数据,如果不存在将创建，设置失效时间（hmset key item1 value1 item2 value2 item3 value3 ...；expire key timeout）
     *
     * @param key     redis key
     * @param hash    map 表中放入数据
     * @param timeout 失效时间（单位：秒，小于等于0 表示 永久有效）
     */
    public void hmSet(String key, Map<HK, HV> hash, long timeout) {
        redisTemplate.opsForHash().putAll(key, hash);

        // 设置 key 失效时间
        expire(key, timeout);
    }

    /**
     * 仅当 hashKey 不存在时才设置
     *
     * @param key     哈希表
     * @param hashKey 字段名
     * @param value   值
     * @return 设置成功返回 true, 设置失败返回 false
     */
    public Boolean hPutIfAbsent(String key, HK hashKey, HV value) {
        return  redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }


    /**
     * 判断是否存在 hash item（hexists key item）
     *
     * @param key  redis key
     * @param item hash item
     * @return 存在返回true，不存在返回 false
     */
    public boolean hasKey(String key, HK item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * 获取 hash item 对应的 value（hget key num）
     *
     * @param key  redis key
     * @param item hash item
     * @return item 对应的 value
     */
    public HV hget(String key, HK item) {
        return (HV) redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取 key 对应的 hash 数据（hgetall key）
     *
     * @param key redis key
     * @return 对应的 hash 数据
     */
    public Map<HK, HV> hmGet(String key) {
        return (Map<HK, HV>) redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除 hash item 对应的项（hdel key item）
     *
     * @param key  redis key
     * @param item hash item
     * @return 返回删除个数
     */
    public long hdel(String key, HK... item) {
        if (null != item && item.length > 0) {
            return redisTemplate.opsForHash().delete(key, item);
        }
        return 0;
    }

    /**
     * 递增，如果不存在,就会创建一个 并把新增后的值返回（hincrby key item by）
     *
     * @param key  redis key
     * @param item hash item
     * @param by   递增量
     * @return 自增后的数量
     */
    public long hincr(String key, HK item, long by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * 递减，如果不存在,就会创建一个 并把递减后的值返回（hincrby key item by）
     *
     * @param key  redis key
     * @param item hash item
     * @param by   递减量
     * @return 递减后的数量
     */
    public long hdecr(String key, HK item, long by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 获取 key 对应的 hash item 集合（hkeys key）
     *
     * @param key redis key
     * @return hash item 集合
     */
    public Set<HK> hItemKeys(String key) {
        return (Set<HK>) redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取 key 对应的 hash value 集合（hvals key）
     *
     * @param key redis key
     * @return hash value 集合
     */
    public List<HV> hItemValues(String key) {
        return (List<HV>) redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取 key 对应的 hash 中元素个数（hlen key）
     *
     * @param key
     * @return hash 中元素个数
     */
    public long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }


    /**
     * 迭代哈希表中的键值对
     *
     * @param key     哈希表
     * @param options 迭代的限制条件, 为 ScanOptions.NONE 则无限制
     * @return 下一个键值对元组的游标
     */
    public Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options) {
        return redisTemplate.opsForHash().scan(key, options);
    }




}
    