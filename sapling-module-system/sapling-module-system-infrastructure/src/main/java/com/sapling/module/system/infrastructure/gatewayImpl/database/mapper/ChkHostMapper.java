package com.sapling.module.system.infrastructure.gatewayImpl.database.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sapling.module.system.infrastructure.gatewayImpl.database.dataobject.ChkHostDO;
import com.sapling.frame.mybatis.core.mapper.BaseMapperX;

import java.util.ArrayList;
import java.util.List;

/**
 * 主机检查Mapper接口
 * 
 * @author mbws
 * @description 针对表【tb_chk_host(主机检查表)】的数据库操作Mapper
 */
public interface ChkHostMapper extends BaseMapperX<ChkHostDO> {
    
    /**
     * 根据主机ID集合批量查询主机信息
     * 
     * @param hostIds 主机ID集合
     * @return 主机列表
     */
    default List<ChkHostDO> selectByHostIds(List<String> hostIds) {
        if (hostIds == null || hostIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectList(new LambdaQueryWrapper<ChkHostDO>()
                .in(ChkHostDO::getHostId, hostIds));
    }


}