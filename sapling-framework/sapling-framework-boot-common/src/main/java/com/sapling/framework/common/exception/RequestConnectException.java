package com.sapling.framework.common.exception;

import lombok.Data;

@Data
public class RequestConnectException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public RequestConnectException() {
        return;
    }

    public RequestConnectException(ErrorCode errorCode) {
        this.code = errorCode.getType();
        this.message = errorCode.getMessage();
    }

    public RequestConnectException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public RequestConnectException setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public RequestConnectException setMessage(String message) {
        this.message = message;
        return this;
    }


}
