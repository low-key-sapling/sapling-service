package com.sapling.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sapling.framework.common.exception.enums.ServiceErrorCodeRange;

/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class BusinessException extends RuntimeException {

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
     * 实际的错误详情
     */
    private String errMsgDetail;

    /**
     * 空构造方法，避免反序列化问题
     */
    public BusinessException() {
        return;
    }

    public BusinessException(ErrorCode errorCode) {
        this.type = errorCode.getType();
        this.message = errorCode.getMessage();
    }

    public BusinessException(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public BusinessException(Integer type, String message,String errMsgDetail) {
        this.type = type;
        this.message = message;
        this.errMsgDetail = errMsgDetail;
    }

    public Integer getType() {
        return type;
    }

    public BusinessException setType(Integer code) {
        this.type = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BusinessException setMessage(String message) {
        this.message = message;
        return this;
    }

}
