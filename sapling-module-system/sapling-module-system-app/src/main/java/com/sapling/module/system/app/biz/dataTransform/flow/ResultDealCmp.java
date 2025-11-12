package com.sapling.module.system.app.biz.dataTransform.flow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;

/**
 * Java Node for resultDeal: print outputJson and mark outputSuccess.
 * This replaces the script-based node definition.
 */
@Slf4j
@LiteflowComponent("resultDeal")
public class ResultDealCmp extends NodeComponent {
    @Override
    public void process() throws Exception {
        SystemContext systemContext = this.getContextBean(SystemContext.class);
        String outputJson = systemContext.get("outputJson");
        boolean success = outputJson != null && outputJson.length() > 2;
        log.info("[resultDeal Java] outputJson={}, success={}", outputJson, success);
        systemContext.set("outputSuccess", success);
        if (outputJson == null) {
            systemContext.set("outputJson", "{}");
        }
    }
}
