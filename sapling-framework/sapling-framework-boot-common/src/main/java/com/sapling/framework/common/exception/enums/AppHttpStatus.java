package com.sapling.framework.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Artisan
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
@Getter
@AllArgsConstructor
public enum AppHttpStatus {
    OK(0, "SUCCESS"),
    ERROR(-1, "服务器走神了..."),

    UPLOAD_EXCEPTION(-1, "上报失败"),
    EXCEPTION(100000, "系统异常"),
    RUNTIME_EXCEPTION(100001, "运行时异常"),
    NULL_POINTER(100002, "空指针异常"),
    ILLEGAL_CLASS_CONVERT(100003, "类转换异常"),
    ILLEGAL_INDEX(100004, "非法索引下标"),
    MISMATCH_PARAMETER(100005, "参数不匹配"),
    MISSING_PARAMETER(100006, "参数缺失"),
    ILLEGAL_METHOD(100007, "非法Method请求"),
    ILLEGAL_PARAMETER(100010, "非法参数"),
    ILLEGAL_NUMBER_FORMAT(100012, "数字格式异常"),
    JSON_PARSE_EXCEPTION(100013, "json数据格式转换异常"),
    DATE_TIME_EXCEPTION(100014, "日期格式转换异常"),
    IO_EXCEPTION(100016, "网络请求异常"),
    NOT_FOUND(100017, "不存在"),

    AUTH_EXCEPTION(200000, "身份认证失败"),
    AUTH_EXPIRE(200001, "身份认证过期"),
    PERMISSION_DENY(20002, "权限不匹配"),
    ACCOUNT_NOT_EXIT(200003, "账号不存"),
    ACCOUNT_PASSWORD_ERROR(200004, "账号/密码错误"),
    VALIDATE_CODE_ERROR(200005, "验证码错误"),
    PHONE_VALIDATE_ERROR(200006, "手机验证码错误"),
    IMAGE_CODE_VALIDATE_ERROR(200007, "图形验证码错误"),

    SERVER_LIMITING_EXCEPTION(200008, "服务访问过于频繁，请稍后再试"),
    SERVER_RETRY_EXCEPTION(200009, "服务不可以重复提交，请稍后再试"),
    SERVER_ILLEGAL_ACCESS(200010, "非法访问"),
    SERVER_CIRCUIT_BREAKER(200011, "触发服务降级处理"),

    DATABASE_EXCEPTION(300000, "数据库异常"),
    REDIS_EXCEPTION(300001, "Redis异常"),

    INIT_EXCEPTION(3180000, "初始化异常"),

    API404_EXCEPTION(404, "远程接口不存在"),
    API500_EXCEPTION(500, "远程接口服务错误"),
    API400_BAD_REQUEST_EXCEPTION(400, "请求参数不正确"),

    /**
     * 业务参数 600000-699999  ，从这里开始定义业务参数
     */
    BIZ_CHANNEL_NOT_EXIST(600000,"消息通道不存在"),
    BIZ_DEVICE_ID_ALG_ERROR(600001,"生成设备ID异常-创建MessageDigest异常"),

    BIZ_PARAMS_EXCEPTION(600006,"参数异常"),

    BIZ_ROOT_ID_IS_NULL_EXCEPTION(600007,"根节点不存在"),


    BIZ_BACK_VERIFY_SIGNATURE_FAILED(600029,"签名失败(后台服务之间)");

    /**
     * 状态码
     */
    private final int status;
    /**
     * 描述字段
     */
    private final String message;

}
