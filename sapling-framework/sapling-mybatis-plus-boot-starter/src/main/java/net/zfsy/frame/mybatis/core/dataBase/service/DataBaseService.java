package net.zfsy.frame.mybatis.core.dataBase.service;



import net.zfsy.frame.mybatis.core.result.TableResult;
import net.zfsy.frame.mybatis.core.result.ColumnsResult;

import javax.validation.constraints.NotNull;
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
