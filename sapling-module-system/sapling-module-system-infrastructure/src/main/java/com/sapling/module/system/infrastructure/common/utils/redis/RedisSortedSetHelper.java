package com.sapling.module.system.infrastructure.common.utils.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Set;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: ZSet类型操作类
 * @mark: show me the code , change the world
 */

@Component
public class RedisSortedSetHelper<T> extends RedisCommonHelper {


    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 新增一个元素
     *
     * @param key
     * @param value 值
     * @param score 分数
     * @return
     */
    public Boolean add(String key, String value, Double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 删除zset元素
     *
     * @param key
     * @param value
     * @return
     */
    public Long del(String key,Object value){
        return redisTemplate.opsForZSet().remove(key,value);
    }

    /**
     * 新增一个集合
     *
     * @param key
     * @param set
     * @return
     */
    public Long addTypedTuple(String key, Set<ZSetOperations.TypedTuple<Object>> set) {
        return redisTemplate.opsForZSet().add(key, set);
    }


    /**
     * 给key的value这个值的【分数】加上data，返回结果为 value最后的分数
     * 如果key中这个value不存在，则相当于是add
     * data 可以为负数
     *
     * @param key
     * @param value
     * @param data
     * @return
     */
    public Double incrementScore(String key, String value, double data) {
        return redisTemplate.opsForZSet().incrementScore(key, value, data);
    }

    /**
     * 返回key中，【分数】大于等于min，小于等于max的元素个数
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long count(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取key中元素的个数
     * 如果key不存在，则返回0
     *
     * @param key
     * @return
     */
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取【下标】范围内的元素集合，下标从0开始
     * start和end不能为负数
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> range(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 返回key中，【分数】大于等于min，小于等于max的元素
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> rangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 在分数范围内进行分页返回对象（包含value和分数）
     * 排序：分数从小到大
     *
     * @param key
     * @param min
     * @param max
     * @param ofset
     * @param count
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max, Long ofset, Long count) {
        if (ofset == null || count == null) {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
        }
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, ofset, count);
    }

    /**
     * 在分数范围内进行分页返回对象（包含value和分数）
     * 排序：分数从大到小
     *
     * @param key
     * @param min
     * @param max
     * @param ofset
     * @param count
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max, Long ofset, Long count) {
        if (ofset == null || count == null) {
            return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
        }
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, ofset, count);
    }

    /**
     * 返回当前key下最大的score对应的对象
     * 排序：分数从大到小
     *
     * @param key
     * @return
     */
    public Object getMaxScoreObject(String key) {
        Set<Object> values = redisTemplate.opsForZSet()
                .reverseRangeByScore(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, 1);

        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.iterator().next();
    }

    /**
     * 返回value 在key中的下标
     * 如果value不在key中则返回null
     *
     * @param key
     * @param value
     * @return
     */
    public Long rank(String key, T value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回value在key中的分数
     * 如果value不在key中则返回null
     *
     * @param key
     * @param value
     * @return
     */
    public Double score(String key, T value) {
        return redisTemplate.opsForZSet().score(key, value);
    }



}
    