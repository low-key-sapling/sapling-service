package com.sapling.module.system.client.components.mq.dto;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 管理端业务消息
 * 与BusMessage不同的是，这个消息是面向管理中心之间交互的
 *
 * @param <T>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizMessage<T> implements MessagePayload {
    /**
     * topic
     */
    private String topic;
    /**
     * 发送时间
     */
    private Long receiveTime;
    /**
     * DeviceId
     */
    private String deviceId;
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