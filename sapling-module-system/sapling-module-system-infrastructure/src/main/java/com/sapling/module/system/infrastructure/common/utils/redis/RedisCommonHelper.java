package com.sapling.module.system.infrastructure.common.utils.redis;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Artisan
 * @version 1.0
 * @Description: Redis 通用命令工具类
 */
@Component
public class RedisCommonHelper {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 指定缓存失效时间（expire key seconds）
     *
     * @param key     建
     * @param timeout 失效时间（单位：秒，小于等于0 表示 永久有效）
     */
    public void expire(String key, long timeout) {
        try {
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException("指定缓存失效时间 异常：", e);
        }
    }

    /**
     * 设置在什么时间过期
     *
     * @param key  要设置的 键
     * @param date 过期时间
     * @return 设置成功返回 true, 设置失败返回 false
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }


    /**
     * 移除 key 的过期时间，key 将持久保持
     *
     * @param key 要移除过期时间的 键
     * @return 移除成功返回 true, 并且该 key 将持久存在, 移除失败返回 false
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }


    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key  要查询剩余过期时间的 键
     * @param unit 时间的单位
     * @return 剩余的过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 返回 key 的剩余的过期时间, 默认时间单位: 秒
     *
     * @param key 要查询剩余过期时间的 键
     * @return 剩余的过期时间, 单位: 秒
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }


    /**
     * 判断 key键 是否存在（exists key）
     *
     * @param key 键
     * @return 存在：true；不存在：false
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 批量删除 key
     *
     * @param keys 要删除的 键 的集合
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 删除key键数组的缓存（del key）
     *
     * @param keys 要删除缓存的key键 数组
     */
    public void del(String... keys) {
        if (null != keys && keys.length > 0) {
            redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(keys));
        }
    }

    /**
     * 按照 key值前缀 批量删除 缓存
     *
     * @param prex key值前缀
     */
    public void delByPrex(String prex) {
        Set<String> keys = redisTemplate.keys(prex);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 查找匹配的 key
     *
     * @param pattern 匹配字符串
     * @return 满足匹配条件的 键 的 Set 集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中
     *
     * @param key     要移动的 键
     * @param dbIndex 要移动到的 db 的序号, 从 0 开始
     * @return 移动成功返回 true, 移动失败返回 false
     */
    public Boolean move(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }


    /**
     * 从当前数据库中随机返回一个 key
     *
     * @return 随机获取的 键 的名称
     */
    public Object randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改 key 的名称
     *
     * @param oldKey 修改前的 键 的名称
     * @param newKey 修改后的 键 的名称
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
     *
     * @param oldKey 修改前的 键 的名称
     * @param newKey 修改后的 键 的名称
     * @return 修改成功返回 true, 修改失败返回 false
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key 要查询类型的 键
     * @return key 的数据类型
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

}
    