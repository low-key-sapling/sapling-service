package com.sapling.frame.mybatis.core.handler;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;

import java.util.Date;

@Slf4j
public class CustomMetaObjectHandler implements MetaObjectHandler {
    private static final String CREATE_USER = "creator";

    private static final String CREATE_TIME = "createTime";

    private static final String UPDATE_USER = "updater";

    private static final String UPDATE_TIME = "updateTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            //为空则设置createUser（BaseEntity)
            if (metaObject.hasSetter(CREATE_USER) && ObjectUtil.isNull(metaObject.getValue(CREATE_USER))) {
                setFieldValByName(CREATE_USER, "-1", metaObject); // TODO 替换为实际用户ID
            }
            //为空则设置createTime（BaseEntity)
            if (metaObject.hasSetter(CREATE_TIME) && ObjectUtil.isNull(metaObject.getValue(CREATE_TIME))) {
                setFieldValByName(CREATE_TIME, new Date(), metaObject);
            }
            updateFill(metaObject);
        } catch (ReflectionException e) {
            log.warn(">>> CustomMetaObjectHandler处理过程中无相关字段，不做处理");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            //设置updateUser（BaseEntity)
            if (metaObject.hasSetter(UPDATE_USER) && ObjectUtil.isNull(metaObject.getValue(UPDATE_USER))) {
                setFieldValByName(UPDATE_USER, "-1", metaObject); //TODO 修改为provider
            }
            //设置updateTime（BaseEntity)
            if (metaObject.hasSetter(UPDATE_TIME) && ObjectUtil.isNull(metaObject.getValue(UPDATE_TIME))) {
                setFieldValByName(UPDATE_TIME, new Date(), metaObject);
            }
        } catch (ReflectionException e) {
            log.warn(">>> CustomMetaObjectHandler处理过程中无相关字段，不做处理");
        }
    }

    /**
     * 获取用户唯一id
     *//*
    private Long getUserUniqueId() {
        try {
            SysLoginUser sysLoginUser = LoginContextHolder.me().getSysLoginUserWithoutException();
            if(ObjectUtil.isNotNull(sysLoginUser)) {
                return sysLoginUser.getId();
            } else {
                return -1L;
            }
        } catch (Exception e) {
            //如果获取不到就返回-1
            return -1L;
        }
    }*/
}
