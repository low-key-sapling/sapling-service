package com.sapling.frame.mybatis.core.mapping;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @description: ZfDatabaseIdProvider 自定义DatabaseIdProvider
 *  mybatis 原DatabaseIdProvider 是根据 dataSource 中的数据库厂商名称，不易用。现整合为用URL来判断，与mbp中的分页插件保持一致
 * @author: lowkey
 * @create: 2022/4/2 09:05
 **/
public class SaplingDatabaseIdProvider implements DatabaseIdProvider {
    private static final Log logger = LogFactory.getLog(SaplingDatabaseIdProvider.class);

    @Override
    public void setProperties(Properties p) {
        return;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        String url = dataSource.getConnection().getMetaData().getURL();
        DbType dbType = JdbcUtils.getDbType(url);
        if (dbType == null){
            logger.warn("The jdbcUrl is " + url + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER.getDb();
        }
        return dbType.getDb();
    }
}
