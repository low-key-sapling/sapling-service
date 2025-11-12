package com.sapling.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sapling.framework.common.exception.enums.ServiceErrorCodeRange;

/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends RuntimeException {

    /**
     * 业务错误码
     *
     * @see ServiceErrorCodeRange
     */
    private Integer type;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
        return;
    }

    public ServiceException(ErrorCode errorCode) {
        this.type = errorCode.getType();
        this.message = errorCode.getMessage();
    }

    public ServiceException(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getCode() {
        return type;
    }

    public ServiceException setCode(Integer type) {
        this.type = type;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

}
