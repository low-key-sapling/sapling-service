package com.sapling.module.system.infrastructure.common.slot;


import com.google.common.collect.Maps;
import com.sapling.framework.common.exception.ErrorCode;
import com.sapling.framework.common.utils.bean.BeanUtils;

import java.util.Map;

/**
 * @author mbws team
 */
public class SystemContext {

    /**
     * LiteFLow上下文参数
     */
    private Map<String, Object> contextData = Maps.newConcurrentMap();

    /**
     * 错误码
     */
    private ErrorCode errorCode;

    public SystemContext() {
        return;
    }

    public SystemContext(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    public <T> T get(String key) {
        return (T) contextData.get(key);
    }

    public Map<String, Object> getAll() {
        return BeanUtils.toBean(contextData, Map.class);
    }

    public SystemContext set(String key, Object value) {
        contextData.put(key, value);
        return this;
    }

    public void error(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void error(Integer errorCode, String errorMsg) {
        this.errorCode = new ErrorCode(errorCode, errorMsg);
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
    