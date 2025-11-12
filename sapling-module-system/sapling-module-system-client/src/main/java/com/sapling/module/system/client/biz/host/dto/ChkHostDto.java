package com.sapling.module.system.client.biz.host.dto;

import lombok.Data;

/**
 * 主机检查数据传输对象
 * 
 * @author mbws
 */
@Data
public class ChkHostDto {
    
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