package com.sapling.module.system.app.biz.dataTransform.executor;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Executor for running the LiteFlow chain: dataTransformScriptChain
 * It prepares input JSON, runs the chain, and fetches the output from SystemContext.
 */
@Slf4j
@Component
public class DataTransformExe {

    @Resource
    private FlowExecutor flowExecutor;


    /**
     * Execute dataTransformScriptChain with a raw input string.
     * @param inputString raw JSON string expected by the chain
     * @return outputJson; returns null if execution failed
     */
    public String transformInputString(String inputString) {
        SystemContext context = new SystemContext();
        context.set("inputString", inputString);

        LiteflowResponse response = flowExecutor.execute2Resp("dataTransformScriptChain", null, context);
        if (!response.isSuccess()) {
            log.error("LiteFlow chain execution failed: code={}, message={}", response.getCode(), response.getMessage());
            return null;
        }
        String outputJson = context.get("outputJson");
        Boolean outputSuccess = context.get("outputSuccess");
        log.info("dataTransformScriptChain output: {}, success={}", outputJson, outputSuccess);
        return outputJson;
    }

}
