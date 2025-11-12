```java
// 基本用法
LogUtils.info("用户{}在{}登录", userId, loginTime);

// 使用format方法
String message = LogUtils.format("用户{}在{}登录", userId, loginTime);
LogUtils.info(message);

// 检查参数匹配
if (LogUtils.isArgsMatch("用户{}在{}登录", userId, loginTime)) {
    LogUtils.info("用户{}在{}登录", userId, loginTime);
} else {
    LogUtils.warn("日志参数不匹配");
}

// 多个参数
LogUtils.debug("用户{}在{}使用{}设备登录，IP地址为{}", 
    userId, loginTime, deviceType, ipAddress);

// 带异常信息
LogUtils.error("用户{}在{}登录失败，原因：{}", 
    userId, loginTime, "密码错误", exception);
```