package com.sapling.module.system.app.biz.dataTransform.flow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.domain.biz.transform.gateway.TransformGateway;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@LiteflowComponent("esQueryChildDocs")
public class EsQueryChildDocsCmp extends NodeComponent {

    @Resource
    private TransformGateway transformGateway;

    @Override
    public void process() throws Exception {
        SystemContext ctx = this.getFirstContextBean();
        @SuppressWarnings("unchecked")
        List<String> fileCodes = (List<String>) ctx.get("fileCodes");
        if (fileCodes == null || fileCodes.isEmpty()) {
            log.warn("LiteFlow esQueryChildDocs: fileCodes is empty");
            ctx.set("esChildDocsJson", "[]");
            return;
        }
        String json ="";
        ctx.set("esChildDocsJson", json);
    }
}
