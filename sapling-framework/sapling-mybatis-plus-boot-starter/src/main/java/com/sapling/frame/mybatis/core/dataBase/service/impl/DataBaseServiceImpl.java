package com.sapling.frame.mybatis.core.dataBase.service.impl;

import com.sapling.frame.mybatis.core.dataBase.mapper.DataBaseMapper;
import com.sapling.frame.mybatis.core.dataBase.service.DataBaseService;
import com.sapling.frame.mybatis.core.result.TableResult;
import com.sapling.frame.mybatis.core.result.ColumnsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 数据库service
 * @author: neo
 * @date: 2022/4/7 09:34
 */
@Service
public class DataBaseServiceImpl implements DataBaseService {
    @Autowired
    private DataBaseMapper dataBaseMapper;

    /**
     * 查询指定库中所有表
     * @param dbName 数据库实例名
     * @return 指定实例下的表
     */
    @Override
    public List<TableResult> selectInformationTable(@NotNull String dbName) {
        return dataBaseMapper.selectInformationTable(dbName);
    }

    /**
     * 查询指定表中所有字段
     * @param dbName 数据库实例名
     * @param tableName 表名
     * @return 指定表中的字段
     */
    @Override
    public List<ColumnsResult> selectInformationColumns(@NotNull String dbName, @NotNull String tableName) {
        return dataBaseMapper.selectInformationColumns(dbName, tableName);
    }
}
