package net.zf.module.system.app.biz.dataTransform.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import net.zf.module.system.domain.biz.transform.gateway.TransformGateway;
import net.zf.module.system.infrastructure.common.slot.SystemContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@LiteflowComponent("esQueryMGChildDocs")
public class EsQueryMGChildDocsCmp extends NodeComponent {

    @Resource
    private TransformGateway transformGateway;

    @Override
    public void process() throws Exception {
        List<Object> esChildDocsList = new ArrayList<>();

        SystemContext ctx = this.getFirstContextBean();
        @SuppressWarnings("unchecked")
        List<String> fileCodes = (List<String>) ctx.get("fileCodes");
        if (fileCodes == null || fileCodes.isEmpty()) {
            log.warn("LiteFlow esQueryMGChildDocs: fileCodes is empty");
            ctx.set("esChildDocsList", esChildDocsList);
            return;
        }
        ctx.set("esChildDocsList", transformGateway.getMGFileInfoByFileCode(fileCodes));
    }
}
