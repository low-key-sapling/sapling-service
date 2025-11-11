package net.zf.module.system.domain.components.operatelog.gateway;

import net.zf.module.system.domain.components.operatelog.model.OperationLogEntity;
import org.springframework.scheduling.annotation.Async;


public interface OperationLogGateway {
    /**
     * 保存操记录
     *
     * @param operationLogEntity operationLog Domain
     */
    @Async
    void save(OperationLogEntity operationLogEntity);
}
