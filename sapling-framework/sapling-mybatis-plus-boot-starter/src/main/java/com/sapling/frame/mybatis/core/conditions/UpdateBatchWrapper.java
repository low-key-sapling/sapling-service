package com.sapling.frame.mybatis.core.conditions;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Arrays;
import java.util.List;

public class UpdateBatchWrapper<T> extends AbstractLambdaWrapper<T, UpdateBatchWrapper<T>> {

    /**
     * 需要更新的字段
     */
    private List<String> updateFields = null;

    @Override
    protected UpdateBatchWrapper<T> instance() {
        return null;
    }

    /**
     * 关键代码,为属性设置值
     */
    @SafeVarargs
    public final UpdateBatchWrapper<T> setUpdateFields(SFunction<T, ?>... columns) {
        this.updateFields = Arrays.asList(columnsToString(columns).split(","));
        return this;
    }

    public List<String> getUpdateFields() {
        return updateFields;
    }

}
