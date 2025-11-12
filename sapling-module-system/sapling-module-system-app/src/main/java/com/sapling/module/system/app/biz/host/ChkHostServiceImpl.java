package com.sapling.module.system.app.biz.host;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.client.biz.host.IChkHostService;
import com.sapling.module.system.client.biz.host.dto.ChkHostDto;
import com.sapling.module.system.domain.biz.host.gateway.ChkHostGateway;
import com.sapling.module.system.domain.biz.host.model.ChkHostEntity;
import com.sapling.module.system.infrastructure.convertor.ChkHostConvertor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 主机检查服务实现
 *
 * @author mbws
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChkHostServiceImpl implements IChkHostService {

    private final ChkHostGateway chkHostGateway;


    @Override
    public List<ChkHostDto> listByHostIds(List<String> hostIds) {
        if (hostIds == null || hostIds.isEmpty()) {
            log.warn("主机ID集合为空，返回空列表");
            return new ArrayList<>();
        }

        log.info("开始批量查询主机信息，ID数量: {}", hostIds.size());

        try {
            List<ChkHostEntity> chkHostEntityList = chkHostGateway.listByHostIds(hostIds);
            List<ChkHostDto> result = ChkHostConvertor.INSTANCE.toDtoList(chkHostEntityList);

            log.info("批量查询主机信息完成，返回{}条记录", result.size());
            return result;

        } catch (Exception e) {
            log.error("批量查询主机信息失败，ID数量: {}", hostIds.size(), e);
            throw new RuntimeException("批量查询主机信息失败", e);
        }
    }

}