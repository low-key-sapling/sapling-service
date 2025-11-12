package com.sapling.module.system.infrastructure.gatewayImpl;

import com.sapling.module.system.infrastructure.convertor.ChkHostConvertor;
import com.sapling.module.system.infrastructure.gatewayImpl.database.dataobject.ChkHostDO;
import com.sapling.module.system.infrastructure.gatewayImpl.database.mapper.ChkHostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.domain.biz.host.gateway.ChkHostGateway;
import com.sapling.module.system.domain.biz.host.model.ChkHostEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主机检查网关实现
 * 
 * @author mbws
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChkHostGatewayImpl implements ChkHostGateway {
    
    private final ChkHostMapper chkHostMapper;
    
    /**
     * 批量查询的批次大小
     */
    private static final int BATCH_SIZE = 1000;
    

    
    @Override
    public List<ChkHostEntity> listByHostIds(List<String> hostIds) {
        if (hostIds == null || hostIds.isEmpty()) {
            log.warn("主机ID集合为空，返回空列表");
            return new ArrayList<>();
        }
        
        log.info("开始批量查询主机信息，总数量: {}", hostIds.size());
        
        List<ChkHostDO> allResults = new ArrayList<>();
        
        // 分批处理，每批1000个
        for (int i = 0; i < hostIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, hostIds.size());
            List<String> batchIds = hostIds.subList(i, endIndex);
            
            log.debug("处理第{}批，范围: {}-{}, 数量: {}", 
                     (i / BATCH_SIZE + 1), i, endIndex - 1, batchIds.size());
            
            try {
                List<ChkHostDO> batchResults = chkHostMapper.selectByHostIds(batchIds);
                allResults.addAll(batchResults);
                
                log.debug("第{}批查询完成，查询到{}条记录", 
                         (i / BATCH_SIZE + 1), batchResults.size());
                
            } catch (Exception e) {
                log.error("第{}批查询失败，跳过该批次，错误: {}", 
                         (i / BATCH_SIZE + 1), e.getMessage(), e);
            }
        }
        
        log.info("批量查询完成，总共查询到{}条记录", allResults.size());
        
        return allResults.stream()
                .map(ChkHostConvertor.INSTANCE::toEntity)
                .collect(Collectors.toList());
    }
}