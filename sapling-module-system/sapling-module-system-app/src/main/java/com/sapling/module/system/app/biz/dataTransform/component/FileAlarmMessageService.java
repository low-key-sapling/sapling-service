package com.sapling.module.system.app.biz.dataTransform.component;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.app.biz.dataTransform.executor.DataTransformExe;
import com.sapling.module.system.domain.biz.transform.gateway.TransformGateway;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * MBWS文件判断消息处理服务
 *
 * @author mbws
 */
@Slf4j
@Service
public class FileAlarmMessageService {
    @Resource
    private DataTransformExe dataTransformExe;
    @Resource
    private TransformGateway transformGateway;

    /**
     * 处理MBWS文件判断消息
     *
     * @param message 消息内容
     */
    public void processFileAlarmMessage(String message) {
        try {
            log.debug("开始处理MBWS文件告警消息: {}", message);
            JSONObject jsonObject = JSONObject.parseObject(message);
            // 只有当judge_flag为2时，才进行数据转化，否则不进行数据转化，直接返回
            if (jsonObject.containsKey("judge_flag") && !jsonObject.getString("judge_flag").equals("2")) {
                return;
            }
            String outputString = dataTransformExe.transformInputString(message);
            transformGateway.sendMessage(outputString);

        } catch (Exception e) {
            log.error("处理MBWS文件判断消息失败: {}", message, e);
            throw e;
        }
    }


}