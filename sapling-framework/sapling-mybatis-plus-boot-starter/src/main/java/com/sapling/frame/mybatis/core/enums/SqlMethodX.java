package com.sapling.frame.mybatis.core.enums;

public enum SqlMethodX {
    UPDATE_BATCH_BY_ID("updateBatchById", "通过主键批量更新数据", "<script>UPDATE %s \n%s \nWHERE %s IN %s\n</script>");

    private final String method;
    private final String desc;
    private final String sql;

    SqlMethodX(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return this.method;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getSql() {
        return this.sql;
    }

}
