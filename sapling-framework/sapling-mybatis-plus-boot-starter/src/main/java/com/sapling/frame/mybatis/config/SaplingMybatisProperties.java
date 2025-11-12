package com.sapling.frame.mybatis.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: 自定义属性
 * @author: neo
 * @date: 2022/4/2 09:16
 */
@ConfigurationProperties("sapling.mybatis")
@Getter
@Setter
@NoArgsConstructor
public class SaplingMybatisProperties {
    /**
     * 是否开启默认字段填充，默认为false
     * 开启后，会自动在执行  insert 和 update 语句时，自动插入或更新
     * createTime updateTime 字段
     */
    private Boolean commonFieldValueAutoFillEnable=false;

    /**
     * 配置程序的根目录
     */
    private String basePackage;
}
