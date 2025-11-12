package com.sapling.module.system.app.biz.dataTransform.component;

import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.domain.biz.transform.gateway.TransformGateway;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * MBWS文件判断消息处理服务（LiteFlow 形式）
 * 根据 sourceType 选择流程 -> ES 查询 -> 数据转换 -> 发送消息
 */
@Slf4j
@Service
public class FileJudgeMessageService {
    @Resource
    private FlowExecutor flowExecutor;

    @Resource
    private TransformGateway transformGateway;

    /**
     * 处理MBWS文件判断消息
     * @param message 消息内容
     */
    public void processFileJudgeMessage(String message) {
        try {
            log.info("开始处理MBWS文件判定消息: {}", message);


            JSONObject judgeMessage = JSONObject.parseObject(message);
            // 只有当judge_flag为2时，才进行数据转化，否则不进行数据转化，直接返回
            if (judgeMessage.containsKey("judgeFlag") && !judgeMessage.getString("judgeFlag").equals("2")) {
                return;
            }

            List<String> fileCodes = judgeMessage.getJSONArray("fileCodes").toJavaList(String.class);
            String sourceType = judgeMessage.getString("sourceType"); // "wj" or "mg"

            // 准备 LiteFlow 上下文
            SystemContext context = new SystemContext();
            context.set("fileCodes", fileCodes);
            context.set("sourceType", sourceType);
            context.set("inputString", judgeMessage.toJSONString());
            LiteflowResponse response = flowExecutor.execute2Resp("fileJudgeChain", null, context);
            if (!response.isSuccess()) {
                log.error("LiteFlow 执行失败: code={}, message={}", response.getCode(), response.getMessage());
                return;
            }
            log.info("MBWS文件判断消息处理完成：fileCodes:{}", fileCodes);
        } catch (Exception e) {
            log.error("处理MBWS文件判断消息失败: {}", message, e);
        }
    }
}
