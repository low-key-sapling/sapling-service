package com.sapling.framework.core.entity;

import lombok.Getter;

/**
 * 预定义错误码枚举示例
 */
@Getter
public enum ErrorCode {
    // 参数校验失败的错误码
    INVALID_PARAM(40001, "参数校验失败"),
    // 认证失败的错误码
    AUTH_FAILED(40101, "认证失败"),
    // 权限不足的错误码
    PERMISSION_DENIED(40301, "权限不足"),
    // 资源不存在的错误码
    RESOURCE_NOT_FOUND(40401, "资源不存在"),
    // 系统繁忙的错误码
    INTERNAL_ERROR(50001, "系统繁忙");

    /**
     * -- GETTER --
     *  获取错误码
     *
     * @return 错误码
     */
    // 错误码
    private final int code;
    /**
     * -- GETTER --
     *  获取错误消息
     *
     * @return 错误消息
     */
    // 错误消息
    private final String message;

    /**
     * 构造错误码枚举实例
     *
     * @param code    错误码
     * @param message 错误消息
     */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}