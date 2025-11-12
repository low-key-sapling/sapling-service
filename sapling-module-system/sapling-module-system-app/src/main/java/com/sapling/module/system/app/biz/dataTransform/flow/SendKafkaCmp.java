package com.sapling.module.system.app.biz.dataTransform.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.domain.biz.transform.gateway.TransformGateway;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;

import javax.annotation.Resource;

/**
 * LiteFlow 节点：发送 Kafka 消息（支持批量）
 * outputJson 可能是 JSON 数组（批量）或 JSON 对象（单条）。
 */
@Slf4j
@LiteflowComponent("sendKafka")
public class SendKafkaCmp extends NodeComponent {

    @Resource
    private TransformGateway transformGateway;

    @Override
    public void process() throws Exception {
        SystemContext systemContext = this.getFirstContextBean();
        // 仅做 JSON 转字符串并逐条发送
        JSONArray outputJsonArray = systemContext.get("outputJsonArray");
        if (outputJsonArray == null) {
            outputJsonArray = new JSONArray();
            String  outputJson = systemContext.get("outputJson");
            outputJsonArray.add(outputJson);
        }
        if (outputJsonArray.isEmpty()) {
            return;
        }

        for (Object obj : outputJsonArray) {
            transformGateway.sendMessage(JSON.toJSONString(obj));
        }
    }
}
