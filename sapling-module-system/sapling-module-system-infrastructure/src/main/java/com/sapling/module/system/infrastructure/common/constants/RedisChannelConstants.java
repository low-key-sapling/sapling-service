package com.sapling.module.system.infrastructure.common.constants;

/**
 * key命名 规范如下  epc:模块名称:业务key名称
 * 模块名称可选项 bus   biz  data
 *
 * 均为小写
 */
public class RedisChannelConstants {
    public static final String R_KEY_REG_INFO = "epc:bus:reg_info";
    public static final String ORG_CACHE_UPDATE_CHANNEL = "epc:biz:org_cache_update";
}
