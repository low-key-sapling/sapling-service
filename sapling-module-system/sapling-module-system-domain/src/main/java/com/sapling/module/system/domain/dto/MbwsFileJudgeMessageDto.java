package com.sapling.module.system.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * MBWS文件判断消息DTO
 * 
 * @author mbws
 */
@Data
public class MbwsFileJudgeMessageDto {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件名称
     */
    private String fileName;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件MD5
     */
    private String fileMd5;
    
    /**
     * 判断结果
     */
    private String judgeResult;
    
    /**
     * 判断类型
     */
    private String judgeType;
    
    /**
     * 风险等级
     */
    private String riskLevel;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 判断时间
     */
    private LocalDateTime judgeTime;
    
    /**
     * 原始消息
     */
    private String rawMessage;
}