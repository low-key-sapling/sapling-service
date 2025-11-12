package com.sapling.framework.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItxResponse<T> {
    private int ret;

    private String errorcode;

    private String msg;

    private T data;
}
