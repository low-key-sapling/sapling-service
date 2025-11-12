package com.sapling.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sapling.framework.common.exception.ErrorCode;
import com.sapling.framework.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回
 *
 * @author mbws
 * @param <T> 数据泛型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     *
     * @see ErrorCode#getType()
     */
    private Integer type;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 错误提示，用户可阅读
     *
     * @see ErrorCode#getMessage() ()
     */
    private String message;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     * <p>
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getType(), result.getMessage());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getType().equals(code), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.type = code;
        result.message = message;
        return result;
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getType(), errorCode.getMessage());
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.type = GlobalErrorCodeConstants.SUCCESS.getType();
        result.data = data;
        result.message = "success";
        return result;
    }

    public static <T> CommonResult<T> success(T data, String msg) {
        CommonResult<T> result = new CommonResult<>();
        result.type = GlobalErrorCodeConstants.SUCCESS.getType();
        result.data = data;
        result.message = msg;
        return result;
    }

    public static <T> CommonResult<T> success(String message) {
        CommonResult<T> result = new CommonResult<>();
        result.type = GlobalErrorCodeConstants.SUCCESS.getType();
        result.message = message;
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCodeConstants.SUCCESS.getType());
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isSuccess() {
        return isSuccess(type);
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isError() {
        return !isSuccess();
    }

    // ========= 和 Exception 异常体系集成 =========

    /**
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     */
    public void checkError() throws ServiceException {
        if (isSuccess()) {
            return;
        }
        // 业务异常
        throw new ServiceException(type, message);
    }

    public static <T> CommonResult<T> error(ServiceException serviceException) {
        return error(serviceException.getType(), serviceException.getMessage());
    }


    /**
     * 成功响应（默认200状态码）
     */
    public static <T> ResponseEntity<CommonResult<T>> successNew(T data) {
        return success(HttpStatus.OK.value(), "success", data, HttpStatus.OK);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ResponseEntity<CommonResult<T>> success(String message, T data) {
        return success(HttpStatus.OK.value(), message, data, HttpStatus.OK);
    }

    /**
     * 成功响应（自定义状态码和HTTP状态）
     */
    public static <T> ResponseEntity<CommonResult<T>> success(int type, String message, T data, HttpStatus httpStatus) {
        CommonResult<T> result = new CommonResult<>(type, data, message);
        return new ResponseEntity<>(result, httpStatus);
    }

    /**
     * 失败响应（业务错误）
     */
    public static <T> ResponseEntity<CommonResult<T>> fail(int code, String message) {
        return fail(code, message, null, HttpStatus.BAD_REQUEST);
    }

    /**
     * 完全自定义失败响应
     */
    public static <T> ResponseEntity<CommonResult<T>> fail(int code, String message, T data, HttpStatus httpStatus) {
        CommonResult<T> result = new CommonResult<>(code, data, message);
        return new ResponseEntity<>(result, httpStatus);
    }

    /**
     * 标准错误响应（带错误数据）
     */
    public static <T> ResponseEntity<CommonResult<T>> error(ErrorCode errorCode, T data, HttpStatus httpStatus) {
        CommonResult<T> result = new CommonResult<>(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getType(), data,errorCode.getMessage());
        return new ResponseEntity<>(result, httpStatus);
    }

}
