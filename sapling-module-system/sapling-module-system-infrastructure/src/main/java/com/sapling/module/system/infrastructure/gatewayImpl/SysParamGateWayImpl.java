package com.sapling.module.system.infrastructure.gatewayImpl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sapling.module.system.infrastructure.common.constants.RedisKeyConstants;
import com.sapling.module.system.infrastructure.common.constants.enums.SysParamEnum;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.domain.biz.system.gateway.SysParamGateWay;
import com.sapling.module.system.domain.biz.system.model.SysParamEntity;
import com.sapling.framework.common.exception.BasicException;
import com.sapling.framework.common.exception.enums.AppHttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * 系统参数网关实现
 * 
 * @author zf
 * @since 2024-03-20
 */
@Slf4j
@Component
public class SysParamGateWayImpl implements SysParamGateWay {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private Cache<String, SysParamEntity> localCache;

    @PostConstruct
    public void init() {
        localCache = CacheBuilder.newBuilder()
                // 最大并发级别为CPU核心数
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                // 最大缓存Key个数
                .maximumSize(SysParamEnum.values().length + 100)
                // 初始化缓存个数
                .initialCapacity(SysParamEnum.values().length)
                // 构建
                .build();
    }

    @Override
    public SysParamEntity getParamById(String id) {
        SysParamEntity sysParamEntity;
        try {
            sysParamEntity = localCache.get(id, () -> getSysParamFromRemote(id));
        } catch (ExecutionException e) {
            log.error("获取系统参数失败, id: {}", id, e);
            throw new BasicException(AppHttpStatus.BIZ_PARAMS_EXCEPTION);
        }
        return sysParamEntity;
    }

    /**
     * 从Redis获取系统参数
     */
    private SysParamEntity getSysParamFromRemote(String id) {
        // 本地缓存未命中，从Redis获取
        String paramJSONStr = (String) stringRedisTemplate.opsForHash().get(RedisKeyConstants.ItxCacheKey.SYS_PARAM, id);
        if (ObjectUtil.isEmpty(paramJSONStr)) {
            // 获取参数名称
            String paramName = Arrays.stream(SysParamEnum.values())
                    .filter(paramEnum -> paramEnum.getId().equals(id))
                    .findFirst()
                    .map(SysParamEnum::getParamName)
                    .orElse("");
            log.error("系统参数【{}】不存在", id + (ObjectUtil.isNotEmpty(paramName) ? "(" + paramName + ")" : ""));
            return new SysParamEntity();
        }
        return JSONUtil.toBean(paramJSONStr, SysParamEntity.class);
    }

    @Override
    public String getParamById(String id, String defaultValue) {
        SysParamEntity paramById = getParamById(id);
        if (ObjectUtil.isNull(paramById) || ObjectUtil.isEmpty(paramById.getParamValue())) {
            return defaultValue;
        }
        return paramById.getParamValue();
    }

    @Override
    public String getZmct() {
        SysParamEntity param = getParamById(SysParamEnum.ZMCT.getId());
        if (param == null || StringUtils.isBlank(param.getParamValue())) {
            log.error("获取zmct参数失败");
            return null;
        }
        return param.getParamValue();
    }

    /**
     * 清除本地缓存
     *
     * @param id 参数ID
     */
    public void invalidateLocalCache(String id) {
        try {
            localCache.invalidate(id);
            log.info("已清除系统参数本地缓存，参数ID：{}", id);
        } catch (Exception e) {
            log.error("清除系统参数本地缓存失败，参数ID：{}, 异常信息:{}", id, ExceptionUtils.getStackTrace(e));
        }
    }
} 