package com.sapling.module.system.domain.components.delayqueue.gateway;

public interface DelayQueueGateway {

    /**
     * 将设备ID添加到延迟队列中
     *
     * @param delayTimes 延迟时间，单位为毫秒，表示消息应在多少时间后被处理
     * @param content 消息内容，通常包含设备ID等相关信息
     */
     void addJob2DelayQueueWithDelayTime(long delayTimes, String content) ;


    /**
     * 异步删除一个延时任务
     *
     * @param messageId
     */
     void deleteMsgFromDelayQueue(String messageId);

}
