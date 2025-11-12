package com.sapling.framework.core.entity;

import java.io.Serializable;

@lombok.Data
@lombok.AllArgsConstructor
public  class Result<T> implements Serializable {
    private final int type;
    private final String message;
    private final T data;

    /**
     * 无参构造函数
     */
    public Result() {
        this(0, null, null);
    }
}