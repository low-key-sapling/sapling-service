package com.sapling.module.system.domain.biz.transform.gateway;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 数据转换获取数据
 */
public interface TransformGateway {
    /**
     * @param fileCode 文件code
     * @return java.lang.String JSONString
     * @author yuanjifan
     * @description 根据filecode获取相关副本(判定违规)
     * @date 2025/10/22 9:23
     */
    List<JSONObject> getWJFileInfoByFileCode(List<String> fileCode);

    /**
     * @param fileCode 文件code
     * @return java.lang.String JSONString
     * @author yuanjifan
     * @description 根据filecode获取相关副本(判定违规)
     * @date 2025/10/22 9:23
     */
    List<JSONObject> getMGFileInfoByFileCode(List<String> fileCode);

    /**
     * @param message 转换后的告警消息 JSONString
     * @author yuanjifan
     * @description 发送告警消息
     * @date 2025/10/23 10:04
     */
    void sendMessage(String message);
}