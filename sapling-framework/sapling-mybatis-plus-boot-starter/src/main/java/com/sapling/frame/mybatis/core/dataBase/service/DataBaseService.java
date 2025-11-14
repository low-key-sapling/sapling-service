package com.sapling.frame.mybatis.core.dataBase.service;



import com.sapling.frame.mybatis.core.result.TableResult;
import com.sapling.frame.mybatis.core.result.ColumnsResult;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 数据库常规查询操作
 * @author: neo
 * @date: 2022/4/6 15:41
 */
public interface DataBaseService {
    List<TableResult> selectInformationTable(@NotNull String dbName);

    List<ColumnsResult> selectInformationColumns(@NotNull String dbName, @NotNull String tableName);
}
