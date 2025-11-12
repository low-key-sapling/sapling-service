package com.sapling.framework.core.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.common.exception.enums.AppHttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * 符合RESTful规范的HTTP响应工具类
 */
@Slf4j
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    /**
     * 错误码
     *
     * @see ErrorCode-getType()
     */
    private Integer type;

    /**
     * 错误提示，用户可阅读
     *
     * @see ErrorCode#getMessage() ()
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 无参构造函数
     */
    public ApiResponse() {
        this(0, null, null);
    }

    /**
     * 成功响应（默认200状态码和自定义消息）
     * 此方法用于创建一个表示成功响应的ResponseEntity对象，包含一个Result对象作为主体
     * 主要用途是简化响应的构建过程，通过提供一个消息参数来自定义成功消息
     *
     * @param <T>     泛型参数，表示Result对象中data字段的类型
     * @param message 成功响应的消息
     * @return 包含自定义消息和默认状态码200的ResponseEntity对象
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return success(ResultCode.SUCCESS, message, null, HttpStatus.OK);
    }


    /**
     * 成功响应（默认200状态码）
     * 此方法用于创建一个表示成功响应的ResponseEntity对象，包含成功状态码、消息和数据
     * 它简化了成功响应的构建过程，通过封装通用的成功响应格式，提高了代码复用性和可维护性
     *
     * @param <T>  泛型参数，表示返回数据的类型
     * @param data 响应中携带的数据，可以是任何类型
     * @return 包含成功状态码、消息和数据的ResponseEntity对象
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(ResultCode.SUCCESS, "success", data, HttpStatus.OK);
    }


    /**
     * 成功响应（自定义消息）
     *
     * @param message 自定义的成功消息
     * @param data    响应的数据
     * @param <T>     数据的类型
     * @return 包含成功消息和数据的ResponseEntity对象
     * <p>
     * 此方法用于创建一个表示成功响应的ResponseEntity对象，可以自定义消息内容和响应的数据。
     * 它使用了泛型来允许响应各种类型的数据，并将结果包装在Result对象中，以便统一响应格式。
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return success(ResultCode.SUCCESS, message, data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, String encryptedData) {
        return success(ResultCode.SUCCESS, message, (T) encryptedData, HttpStatus.OK);
    }


    /**
     * 成功响应（自定义状态码和HTTP状态）
     * <p>
     * 此方法用于创建一个表示成功响应的ResponseEntity对象，允许调用者自定义响应的状态码、消息内容、数据以及HTTP状态
     * 它封装了一个Result对象和指定的HTTP状态，便于在RESTful API中返回给客户端
     *
     * @param type       响应类型，用于标识响应的种类，例如成功、错误等
     * @param message    响应消息，提供更详细的响应信息
     * @param data       响应数据，包含客户端请求的数据结果
     * @param httpStatus HTTP状态码，指示HTTP响应的状态
     * @param <T>        泛型参数，表示响应数据的类型
     * @return 返回一个包含Result对象和指定HTTP状态的ResponseEntity对象
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(int type, String message, T data, HttpStatus httpStatus) {
        ApiResponse<T> result = new ApiResponse<>(type, message, data);
        return new ResponseEntity<>(result, httpStatus);
    }


    /**
     * 创建一个成功的HTTP响应，包含指定的数据
     * 该方法用于当需要返回一个简单的成功响应，且不需要进行数据格式转换时
     *
     * @param <T>  泛型参数，表示响应体中数据的类型
     * @param data 响应体中的数据，可以是任意类型
     * @return 返回一个包含指定数据的ResponseEntity对象，HTTP状态码为200（OK）
     */
    public static <T> ResponseEntity<T> successOnlyContent(T data) {
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    /**
     * 创建一个表示失败响应的Result对象，并包装在ResponseEntity中返回
     * 该方法用于当API调用失败时，返回一个统一格式的HTTP响应
     *
     * @param message 失败的错误信息，用于告知调用者失败的原因
     * @return 包含失败信息的ResponseEntity对象，状态码为INTERNAL_SERVER_ERROR
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(String message) {
        return fail(ResultCode.FAILURE, message, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * 创建一个表示失败响应的方法，用于处理业务错误
     * 此方法提供了一个泛型方法，允许在不成功的情况下返回一致的响应格式
     * 通过封装错误类型、错误消息以及可选的数据，使得API的消费者可以更容易地处理错误情况
     *
     * @param type    错误类型，用于标识错误的种类
     * @param message 错误消息，描述错误的详细信息
     * @return 包含错误信息的ResponseEntity对象，HTTP状态码为BAD_REQUEST
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(int type, String message) {
        return fail(type, message, null, HttpStatus.BAD_REQUEST);
    }

    /**
     * 创建一个表示失败响应的ResponseEntity对象
     * 此方法用于简化失败响应的创建过程，使用给定的AppHttpStatus和HttpStatus
     *
     * @param appHttpStatus 自定义状态码和消息，用于表示应用程序特定的错误或失败情况
     * @param httpStatus    HTTP状态码，用于表示HTTP响应的状态
     * @return 包含失败信息的ResponseEntity对象
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(AppHttpStatus appHttpStatus, HttpStatus httpStatus) {
        // 调用重载的fail方法，传入AppHttpStatus的状态码和消息，以及null作为数据和给定的HttpStatus
        return fail(appHttpStatus.getStatus(), appHttpStatus.getMessage(), null, httpStatus);
    }


    /**
     * 完全自定义失败响应
     * 此方法用于创建一个自定义的失败响应它允许调用者指定响应的类型、消息内容、数据和HTTP状态码
     * 这在需要对客户端请求作出特定错误提示时非常有用
     *
     * @param type       响应类型，用于标识错误的类型
     * @param message    错误消息，用于提供给客户端详细的错误信息
     * @param data       响应数据，可以是任意类型，用于提供额外的错误信息或状态
     * @param httpStatus HTTP状态码，用于设置HTTP响应的状态
     * @return 返回一个包含自定义失败信息的ResponseEntity对象
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(int type, String message, T data, HttpStatus httpStatus) {
        ApiResponse<T> result = new ApiResponse<>(type, message, data);
        return new ResponseEntity<>(result, httpStatus);
    }


    /**
     * 标准错误响应（带错误数据）
     *
     * @param <T>        泛型参数，表示可以返回任意类型的错误数据
     * @param errorCode  错误代码，包含错误码和错误消息
     * @param data       错误数据，可以是任意类型的数据
     * @param httpStatus HTTP状态码，表示错误的HTTP响应状态
     * @return 返回一个包含错误信息和错误数据的HTTP响应实体
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode errorCode, T data, HttpStatus httpStatus) {
        // 创建一个结果对象，包含错误码、错误消息和错误数据
        ApiResponse<T> result = new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), data);
        // 创建一个HTTP响应实体，包含结果对象和HTTP状态码
        return new ResponseEntity<>(result, httpStatus);
    }


    /**
     * 创建一个包含成功响应和自定义头部的ResponseEntity对象
     * 该方法用于当API请求成功时，返回给客户端一个包含指定数据以及自定义头部信息的响应
     *
     * @param data        API请求的成功响应数据
     * @param headerName  自定义响应头部的名称
     * @param headerValue 自定义响应头部的值
     * @param <T>         数据的泛型类型
     * @return 返回一个包含成功响应数据和自定义头部的ResponseEntity对象
     */
    public static <T> ResponseEntity<ApiResponse<T>> successWithHeader(T data, String headerName, String headerValue) {
        // 创建一个包含成功响应数据的ApiResponse对象
        ApiResponse<T> result = new ApiResponse<>(ResultCode.SUCCESS, "成功", data);
        // 返回一个包含自定义头部和成功响应数据的ResponseEntity对象
        return ResponseEntity.ok()
                .header(headerName, headerValue)
                .body(result);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successWithHeaders(T data, Object headerVO) {
        // 创建一个包含成功响应数据的ApiResponse对象
        ApiResponse<T> result = new ApiResponse<>(ResultCode.SUCCESS, "成功", data);

        // 将headerVO转成map
        Map<String, Object> headerMap = BeanUtil.beanToMap(headerVO);

        // 创建一个HttpHeaders对象，并添加自定义头部
        HttpHeaders httpHeaders = new HttpHeaders();
        headerMap.forEach((key, value) -> httpHeaders.add(key, ObjectUtil.isEmpty(value) ? "" : value.toString()));

        // 返回一个包含自定义头部和成功响应数据的ResponseEntity对象
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(result);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successWithHeader(String headerName, String headerValue) {
        return successWithHeader(null, headerName, headerValue);
    }


}