package com.sapling.framework.core.entity;

import lombok.Data;

/**
 * 定义了一个名为ResultCode的内部静态类，用于统一定义和管理响应码
 * 这种设计有利于代码的可维护性和一致性，将响应码集中管理，便于未来可能的修改和扩展
 */
@Data
public class ResultCode {
    // 表示成功操作的响应码
    public static final int SUCCESS = 0 ;
    // 表示失败操作的响应码
    public static final int FAILURE = 1;
}
