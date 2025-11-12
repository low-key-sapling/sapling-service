package com.sapling.module.system.infrastructure.common.constants;

public class KafkaTopicConstants {
    /**
     * 下划线
     */
    public static final String LINE = "-";
    /**
     * 统一前缀
     */
    private static final String KEY_TAG = "epc";
    /**
     * BIZ前缀
     */
    public static final String DEFAULT_TOPIC = "TOPIC-DEFAULT";
    // 违规判定数据同步主题
    public static final String MBWS_FILE_JUDGE_TOPIC = "mbws-fileJudge";
    // 告警上报违规数据同步主题
    public static final String MBWS_FILE_ALARM_TOPIC = "mbws-alarm";
    // 告警上报违规数据同步目标主题(上海市检：文件违规存储topic：jcy-tyaqglpt-terminal-zfmb-ufs，内容协议的话，JSON字符串；暂定无认证)
    public static final String MBWS_FILE_ALARM_TARGET_TOPIC = "jcy-tyaqglpt-terminal-zfmb-ufs";
}
