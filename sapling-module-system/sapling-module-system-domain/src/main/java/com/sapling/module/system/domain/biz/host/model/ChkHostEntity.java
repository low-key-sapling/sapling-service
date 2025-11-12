package com.sapling.module.system.domain.biz.host.model;

import lombok.Data;

/**
 * 主机检查领域实体
 * 
 * @author mbws
 */
@Data
public class ChkHostEntity {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 主机名称
     */
    private String hostName;
    
    /**
     * 主机IP地址
     */
    private String hostId;

}