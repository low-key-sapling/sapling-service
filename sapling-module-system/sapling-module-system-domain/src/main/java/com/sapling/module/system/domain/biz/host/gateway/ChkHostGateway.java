package com.sapling.module.system.domain.biz.host.gateway;

import com.sapling.module.system.domain.biz.host.model.ChkHostEntity;

import java.util.List;

/**
 * 主机检查网关接口
 * 
 * @author mbws
 */
public interface ChkHostGateway {

    /**
     * 根据主机ID集合批量查询主机信息
     * 
     * @param hostIds 主机ID集合
     * @return 主机列表
     */
    List<ChkHostEntity> listByHostIds(List<String> hostIds);

}