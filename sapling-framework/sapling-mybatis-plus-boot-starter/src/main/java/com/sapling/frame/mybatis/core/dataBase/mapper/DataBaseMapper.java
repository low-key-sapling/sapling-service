package com.sapling.frame.mybatis.core.dataBase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sapling.frame.mybatis.core.result.TableResult;
import com.sapling.frame.mybatis.core.result.ColumnsResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: DataBaseMapper
 * @author: 陈锡光
 * @create: 2022/4/6 15:37
 **/
@Mapper
public interface DataBaseMapper extends BaseMapper {

    /**
     * 查询指定库中所有表
     * @param dbName 数据库实例名
     * @return 指定实例下的表
     */
    List<TableResult> selectInformationTable(@Param("dbName") String dbName);

    /**
     * 查询指定表中所有字段
     * @param dbName 数据库实例名
     * @param tableName 表名
     * @return 指定表中的字段
     */
    List<ColumnsResult> selectInformationColumns(@Param("dbName") String dbName, @Param("tableName") String tableName);
}
