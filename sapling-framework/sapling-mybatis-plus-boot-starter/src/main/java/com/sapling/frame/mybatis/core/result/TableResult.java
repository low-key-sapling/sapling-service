package com.sapling.frame.mybatis.core.result;

/**
 * @description: 数据库表返回对象
 * @author: 陈锡光
 * @create: 2022/4/6 15:32
 **/
public class TableResult {

    /**
     * 表名（字母形式的）
     */
    public String tableName;

    /**
     * 创建时间
     */
    public String createTime;

    /**
     * 更新时间
     */
    public String updateTime;

    /**
     * 表名称描述（注释）（功能名）
     */
    public String tableComment;

    public TableResult() {
        return;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }
}
