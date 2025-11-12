package com.sapling.frame.mybatis.core.injector.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.sapling.frame.mybatis.core.enums.SqlMethodX;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

public class UpdateBatchById extends AbstractMethod {
    public UpdateBatchById() {
        super("updateBatchById");
    }

    public UpdateBatchById(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethodX sqlMethod = SqlMethodX.UPDATE_BATCH_BY_ID;
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), this.sqlSet(tableInfo), tableInfo.getKeyColumn(), this.sqlIn(tableInfo.getKeyProperty()));
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    private String sqlSet(TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        StringBuilder sb = new StringBuilder();

        for (TableFieldInfo fieldInfo : fieldList) {
            sb.append("<if test=\"ew.updateFields.contains(&quot;").append(fieldInfo.getColumn()).append("&quot;)\">")
                    .append(fieldInfo.getColumn()).append(" =\n")
                    .append("CASE ").append(tableInfo.getKeyColumn()).append("\n")
                    .append("<foreach collection=\"list\" item=\"et\" >\n")
                    .append("WHEN #{et.").append(tableInfo.getKeyProperty()).append("} THEN #{et.").append(fieldInfo.getProperty()).append("}\n")
                    .append("</foreach>\n").append("END ,\n")
                    .append("</if>\n");

        }
        return "<set>\n" + sb + "</set>";
    }

    private String sqlIn(String keyProperty) {
        StringBuilder sb = new StringBuilder();
        sb.append("<foreach collection=\"list\" item=\"et\" separator=\",\" open=\"(\" close=\")\">\n")
                .append("#{et.").append(keyProperty).append("}")
                .append("</foreach>\n");

        return sb.toString();
    }
}
