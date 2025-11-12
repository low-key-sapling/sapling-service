package com.sapling.module.system.domain.biz.system.model;

import lombok.Data;

/**
 * 系统参数领域实体
 */
@Data
public class SysParamEntity {
    /**
     * 参数ID
     */
    private String id;
    
    /**
     * 参数名称
     */
    private String paramName;
    
    /**
     * 参数值
     */
    private String paramValue;
}