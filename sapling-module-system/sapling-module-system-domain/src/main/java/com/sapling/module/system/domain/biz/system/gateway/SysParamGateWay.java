package com.sapling.module.system.domain.biz.system.gateway;

import com.sapling.module.system.domain.biz.system.model.SysParamEntity;

/**
 * 系统参数网关接口
 * 
 * @author zf
 * @since 2024-03-20
 */
public interface SysParamGateWay {
    /**
     * 根据参数ID获取系统参数
     *
     * @param id 参数ID
     * @return 系统参数
     */
    SysParamEntity getParamById(String id);

    /**
     * 根据参数ID获取系统参数
     * @param id 参数ID
     * @param defaultValue 默认值
     * @return 系统参数
     */
    String getParamById(String id, String defaultValue);

    /**
     * 获取 zmct
     * @return zmct
     */
    String getZmct();
} 