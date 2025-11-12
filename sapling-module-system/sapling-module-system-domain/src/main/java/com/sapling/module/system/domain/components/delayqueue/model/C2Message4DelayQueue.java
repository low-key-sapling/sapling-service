package com.sapling.module.system.domain.components.delayqueue.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.common.utils.date.DateUtils;

import java.util.Date;

/**
 * @Description 封装了消息执行的相关信息，如执行时间、放置时间及内容等
 * @Author mbws
 */
@Data
@Slf4j
public class C2Message4DelayQueue extends BaseMessage4DelayQueue {

    /**
     * 应当运行的时间
     * 用于指定命令被执行的时间点
     */
    private Date shouldRunTime;

    /**
     * 放置命令的时间
     * 记录命令被添加或安排执行的时间
     */
    private Date putTime;

    /**
     * 命令的内容
     * 描述命令具体需要执行的任务或传递的信息
     */
    private String content;


    /**
     * 显示消息详情
     * 该方法用于记录和显示有关延迟队列中消息的详细信息，包括消息的投放时间、预期运行时间和实际运行时间，以及相对于预期运行时间的延迟
     * 主要用于调试和监控目的
     *
     * @param c2Message4DelayQueue 延迟队列中的消息对象，包含消息的投放时间和预期运行时间等信息
     * @param topic                消息的主题，用于标识消息类型或来源
     */
    public static void showDetails(C2Message4DelayQueue c2Message4DelayQueue, String topic) {
        // 获取消息的投放时间
        long putTime = c2Message4DelayQueue.getPutTime().getTime();
        // 获取消息的预期运行时间
        long needRunTime = c2Message4DelayQueue.getShouldRunTime().getTime();
        // 获取当前时间
        long now = System.currentTimeMillis();
        // 计算当前时间与预期运行时间之间的延迟
        long delayTime = now - needRunTime;

        // 格式化预期运行时间
        String formattedNeedRunTime = DateUtils.formatTime(needRunTime);
        // 格式化当前时间
        String formattedNow = DateUtils.formatTime(now);
        // 格式化投放时间
        String formattedPutTime = DateUtils.formatTime(putTime);

        // 日志输出：包括主题、消息ID、延迟时间、投放时间、预期运行时间和实际运行时间
        log.info("主题:{}, 消息ID: {} 回调成功。 " +
                        "比预期执行推迟了时间:{}毫秒, -- [putTime:{} ,shouldRunTime:{}, 实际RunTime:{}]" +
                        " 消息详情:{}",
                topic,
                c2Message4DelayQueue.getId(),
                delayTime,
                formattedPutTime,
                formattedNeedRunTime,
                formattedNow,
                c2Message4DelayQueue);
    }


}

