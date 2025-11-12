package com.sapling.module.system.infrastructure.common.utils.redis;
 
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: Set类型相关操作
 * @mark: show me the code , change the world
 */
@Component
public class RedisSetHelper<T> extends RedisCommonHelper {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 获取 key键 对应的 Set集合
     *
     * @param key 键
     * @return key键 对应的 Set集合
     */
    public Set<T> members(String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    /**
     * 查找 key键 对应的Set集合中 是否包含value值
     *
     * @param key   键
     * @param value 值
     * @return 包含：true；不包含：false
     */
    public boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放在 Set缓存
     *
     * @param key    键
     * @param values 值数组
     * @return 成功个数
     */
    public long addSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 将数据放在 Set缓存，并设置 失效时间
     *
     * @param key     键
     * @param values  值数组
     * @param timeout 失效时间（单位：秒，小于等于0 表示 永久有效）
     * @return
     */
    public long addSet(String key, T[] values, long timeout) {
        long count = redisTemplate.opsForSet().add(key, values);
        expire(key, timeout);
        return count;
    }

    /**
     * 获取 key键 对应的Set集合的长度
     *
     * @param key 键
     * @return
     */
    public long size(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除 key键 对应的Set集合中 value数组
     *
     * @param key    键
     * @param values 要移除的值数组
     * @return 移除成功的个数
     */
    public long remove(String key, T... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key 集合
     * @return 集合中随机一个元素
     */
    public Object sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 将元素 value 从一个集合移到另一个集合
     *
     * @param key     被移除的集合
     * @param value   要移除的元素
     * @param destKey 移动到的目标集合
     * @return 移动成功返回 true, 移动失败返回 false
     */
    public Boolean sMove(String key, T value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }


    /**
     * 获取 key 集合与多个集合的交集
     *
     * @param key       集合1
     * @param otherKeys 其余多个集合
     * @return 多个集合的交集
     */
    public Set<Object> sIntersect(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * key 集合与 otherKey 集合的交集存储到 destKey 集合中
     *
     * @param key      集合1
     * @param otherKey 集合2
     * @param destKey  用于保存结果的集合
     * @return 新集合的长度
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * key 集合与多个集合的交集存储到 destKey 集合中
     *
     * @param key       集合1
     * @param otherKeys 其余多个集合
     * @param destKey   用于保存结果的集合
     * @return 新集合的长度
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key       集合1
     * @param otherKeys 集合2
     * @return 两个集合的并集
     */
    public Set<Object> sUnion(String key, String otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取 key 集合与多个集合的并集
     *
     * @param key       集合1
     * @param otherKeys 其余多个集合
     * @return 多个集合的并集
     */
    public Set<Object> sUnion(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * key 集合与 otherKey 集合的并集存储到 destKey 中
     *
     * @param key      集合1
     * @param otherKey 集合2
     * @param destKey  用于保存结果的集合
     * @return 新集合的长度
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key 集合与多个集合的并集存储到 destKey 中
     *
     * @param key       集合1
     * @param otherKeys 其余多个集合
     * @param destKey   用于保存结果的集合
     * @return 新集合的长度
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key      集合1
     * @param otherKey 集合2
     * @return 集合1 - 集合2 的差集
     */
    public Set<Object> sDifference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 获取 key 集合与多个集合的差集
     *
     * @param key       集合1
     * @param otherKeys 其余多个集合
     * @return 集合1 - 集合2 - 集合3 - ... 集合n 的差集
     */
    public Set<Object> sDifference(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * key 集合与 otherKey 集合的差集存储到 destKey 中
     *
     * @param key      集合1
     * @param otherKey 集合2
     * @param destKey  用于保存结果的集合
     * @return 新集合的长度
     */
    public Long sDifference(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey,
                destKey);
    }

    /**
     * key 集合与多个集合的差集存储到 destKey 中
     *
     * @param key       集合1
     * @param otherKeys 其余多个集合
     * @param destKey   用于保存结果的集合
     * @return 新集合的长度
     */
    public Long sDifference(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys,
                destKey);
    }



    /**
     * 随机获取集合中的一个元素
     *
     * @param key 集合
     * @return 集合中随机一个元素
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中 count 个元素
     *
     * @param key   集合
     * @param count 要获取的元素个数
     * @return count 个随机元素组成的集合
     */
    public List<Object> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中 count 个元素并且去除重复的
     *
     * @param key   集合
     * @param count 要获取的元素个数
     * @return count 个随机元素组成的集合, 并且不包含重复元素
     */
    public Set<Object> sDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 迭代集合中的元素
     *
     * @param key     集合
     * @param options 迭代的限制条件, 为 ScanOptions.NONE 则无限制
     * @return 下一个元素的游标
     */
    public Cursor<Object> sScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }
}
    