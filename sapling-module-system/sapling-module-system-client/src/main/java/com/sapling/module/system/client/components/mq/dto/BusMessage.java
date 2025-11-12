package com.sapling.module.system.client.components.mq.dto;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息领域模型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusMessage<T> implements MessagePayload {

    /**
     * 唯一键 消息id 不能为空
     */
    private String msgId ;

    /**
     * 发送时间
     */
    private Long receiveTime;

    /**
     * DeviceId
     */
    private String deviceId;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 消息类型
     */
    private String requestURI;

    /**
     * 消息内容
     */
    private T content;


    @Override
    public String contentToJson(Object content) {
        return JSONUtil.toJsonStr(content);
    }

    @Override
    public String busMessageToJson() {
        return JSONUtil.toJsonStr(this);
    }


}