package com.sapling.module.system.client.components.delayqueue;

public interface IDelayQueueService {

    /**
     * 将任务添加到延时队列中
     *
     * @param delayTimes 延时时间，表示任务将在添加后指定时间延迟执行
     * @param content    任务的内容或标识信息
     */
    void addJobToDelayQueue(long delayTimes, String content);

    /**
     * 从延时队列中移除指定的任务
     *
     * @param messageId 要移除的任务的消息ID，用于唯一标识一个任务
     */
    default void removeJobFromDelayQueue(String messageId) {
        // 此方法体目前为空，但提供了扩展的可能性，以便未来实现具体逻辑来移除任务
        return;
    }

}
