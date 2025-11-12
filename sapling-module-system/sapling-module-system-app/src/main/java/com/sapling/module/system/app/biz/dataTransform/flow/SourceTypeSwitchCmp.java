package com.sapling.module.system.app.biz.dataTransform.flow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;

@Slf4j
@LiteflowComponent("sourceTypeSwitch")
public class SourceTypeSwitchCmp extends NodeSwitchComponent {
    @Override
    public String processSwitch() throws Exception {
        SystemContext ctx = this.getFirstContextBean();
        String sourceType = ctx.get("sourceType");
        switch (sourceType.toLowerCase()) {
            case "wj":
                return "esQueryWJChildDocs";
            case "mg":
            default:
                return "esQueryMGChildDocs";
        }
    }
}
