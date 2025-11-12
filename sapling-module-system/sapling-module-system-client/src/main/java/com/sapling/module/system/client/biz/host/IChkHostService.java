package com.sapling.module.system.client.biz.host;

import com.sapling.module.system.client.biz.host.dto.ChkHostDto;

import java.util.List;

/**
 * 主机检查服务接口
 * 
 * @author mbws
 */
public interface IChkHostService {
    
    /**
     * 根据主机ID集合批量查询主机信息
     * 
     * @param hostIds 主机ID集合
     * @return 主机列表
     */
    List<ChkHostDto> listByHostIds(List<String> hostIds);
}