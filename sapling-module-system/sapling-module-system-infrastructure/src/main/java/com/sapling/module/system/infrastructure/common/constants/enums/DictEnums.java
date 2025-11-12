package com.sapling.module.system.infrastructure.common.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DictEnums {
    // 密级
    PUBLIC("0", "公开", 1, "密级"),
    INTERNAL("1", "内部", 1, "密级"),
    SECRET("2", "秘密", 1, "密级"),
    CONFIDENTIAL("3", "机密", 1, "密级"),
    TOP_SECRET("5", "绝密", 1, "密级"),

    // 机器类型
    DESKTOP("0", "台式机", 11, "机器类型"),
    PORTABLE_NOTEBOOK("1", "便携笔记本", 11, "机器类型"),
    PROTECTED_MACHINE("2", "涉密中间机", 11, "机器类型"),

    // 钥匙类型
    ADMIN_KEY("0", "管理员钥匙", 12, "钥匙类型"),
    ACTIVATION_KEY("1", "激活钥匙", 12, "钥匙类型"),

    // 单位类别
    POLICE("1", "政法", 10, "单位类别"),
    ORGANIZATION("10", "组织", 10, "单位类别"),
    TECHNOLOGY("11", "科技", 10, "单位类别"),
    PROPAGANDA("12", "宣传", 10, "单位类别"),
    CULTURE("13", "文化", 10, "单位类别"),
    ECONOMY("14", "经济", 10, "单位类别"),
    RELIGION("15", "宗教", 10, "单位类别"),
    UNITY("2", "统战", 10, "单位类别"),
    DISCIPLINE_INSPECTION("3", "纪检", 10, "单位类别"),
    FINANCE_TAX("4", "财税", 10, "单位类别"),
    AGRICULTURE("5", "农业", 10, "单位类别"),
    EDUCATION("6", "教育", 10, "单位类别"),
    URBAN_BUILDING("7", "城建", 10, "单位类别"),
    COMPREHENSIVE("8", "综合", 10, "单位类别"),
    HEALTH("9", "卫生", 10, "单位类别"),

    // 主机类型
    STANDALONE("1", "单机", 2, "主机类型"),
    NETWORKED("1", "联网", 2, "主机类型"),

    // 主机安装状态
    NOT_INSTALLED("0", "未安装", 3, "主机安装状态"),
    NOT_ACTIVATED("1", "未激活", 3, "主机安装状态"),
    NORMAL("2", "正常", 3, "主机安装状态"),
    TO_BE_DELETED("3", "待注销", 3, "主机安装状态"),
    NORMAL_DELETION("4", "正常注销", 3, "主机安装状态"),
    FORCED_DELETION("5", "强制注销", 3, "主机安装状态"),

    // 在线状态
    OFFLINE("0", "离线", 4, "在线状态"),
    ONLINE("1", "在线", 4, "在线状态"),

    // 主机是否已同步
    NOT_SYNCHRONIZED("0", "未同步", 5, "主机是否已同步"),
    SYNCHRONIZED("1", "已同步", 5, "主机是否已同步"),

    // 其他相关类型
    HOST("0", "主机", 6, "主机是否已同步"),
    HARDWARE("1", "硬件", 6, "主机是否已同步"),
    SOFTWARE("2", "软件", 6, "主机是否已同步"),
    PATCH("3", "补丁", 6, "主机是否已同步"),
    ANTIVIRUS("4", "杀软", 6, "主机是否已同步"),
    OTHER("5", "其他", 6, "主机是否已同步"),
    OTHER_OS("5", "其他", 7, "操作系统类型"),
    WINDOWS("window_32", "Windows", 7, "操作系统类型"),
    CENTOS6("centos6", "CentOS6", 7, "操作系统类型"),
    CENTOS7("centos7", "CentOS7", 7, "操作系统类型"),
    CENTOS8("centos8", "CentOS8", 7, "操作系统类型"),
    DEBIAN7("debian_76", "Debian7", 7, "操作系统类型"),
    DEBIAN8("debian_83", "Debian8", 7, "操作系统类型"),
    FDD32("fdd32", "中科方德 32 位（兆芯、海光）", 7, "操作系统类型"),
    FDD64("fdd64", "中科方德 64 位（兆芯、海光）", 7, "操作系统类型"),
    FDS64("fds64", "中科方德服务器版（兆芯、海光）", 7, "操作系统类型"),
    FT1K5_YH41D("ft1k5_yh41d", "银河麒麟 V4（飞腾）", 7, "操作系统类型"),
    FT1KA_NX4("ft1ka_nx4", "中标麒麟 V4（飞腾）", 7, "操作系统类型"),
    FT_KYLIN10("ft_kylin10", "银河麒麟 V10（飞腾、鲲鹏、海思麒麟）", 7, "操作系统类型"),
    FT_ND7("ft_nd7", "中标麒麟 V7（飞腾）", 7, "操作系统类型"),
    FT_UOS20D("ft_uos20d", "统信 UOS（飞腾、鲲鹏、海思麒麟）", 7, "操作系统类型"),
    KYLIN10("kylin10", "银河麒麟 V10（兆芯、海光）", 7, "操作系统类型"),
    KYLIN4("kylin4", "银河麒麟 V4（兆芯、海光）", 7, "操作系统类型"),
    LA_KYLIN10("la_kylin10", "银河麒麟 V10（龙芯 LoongArch）", 7, "操作系统类型"),
    LA_UOS20D("la_uos20d", "统信 UOS（龙芯 LoongArch）", 7, "操作系统类型"),
    LS_KYLIN10("ls_kylin10", "银河麒麟 V10（龙芯）", 7, "操作系统类型"),
    LS_ND6U632("ls_nd6u632", "中标麒麟 V6（龙芯）", 7, "操作系统类型"),
    LS_NDR7("ls_ndr7", "中标麒麟 V7（龙芯）", 7, "操作系统类型"),
    LS_UOS20D("ls_uos20d", "统信 UOS（龙芯）", 7, "操作系统类型"),
    NS6U5("ns6u5", "中标麒麟 V7（兆芯、海光）", 7, "操作系统类型"),
    NS7("ns7", "中标麒麟 V7（兆芯、海光）", 7, "操作系统类型"),
    SW_NS7("sw_ns7", "中标麒麟 V7（申威）", 7, "操作系统类型"),
    SW_R205B("sw_r205b", "神威睿思（申威）", 7, "操作系统类型"),
    SW_UOS20D("sw_uos20d", "统信 UOS（申威）", 7, "操作系统类型"),
    ZX_FDD86("zx_fdd86", "中科方德（兆芯、海光）", 7, "操作系统类型"),
    ZX_ND664("zx_nd664", "中标麒麟 V6（兆芯）", 7, "操作系统类型"),
    THREE_A1K_ND6U6("3a1k_nd6u6", "中标麒麟 U6（龙芯 3A1000）", 7, "操作系统类型"),
    THREE_A1K_NS6("3a1k_ns6", "中标麒麟服务器版 V6（龙芯）", 7, "操作系统类型"),
    THREE_A2K_DP("3a2k_dp", "深度（龙芯）", 7, "操作系统类型"),
    THREE_A2K_ND6U6("3a2k_nd6u6", "中标麒麟 U6（龙芯 3A2000）", 7, "操作系统类型"),
    THREE_A2K_NS7("3a2k_ns7", "中标麒麟服务器版 V7（龙芯）", 7, "操作系统类型"),
    THREE_B_ND6U6("3b_nd6u6", "中标麒麟 U6（龙芯 3B）", 7, "操作系统类型"),
    THREE_B_ND7("3b_nd7", "龙芯 3B 中标麒麟 7.0", 7, "操作系统类型"),

    // 用户标准密级密和用户密级
    CORE_SECRET("1", "核心涉密", 13, "用户标准密级密"),
    IMPORTANT_SECRET("2", "重要涉密", 13, "用户标准密级密"),
    GENERAL_SECRET("3", "一般涉密", 13, "用户标准密级密"),
    NON_SECRET("4", "非涉密", 13, "用户标准密级密"),
    TOP_SECRET_USER("5", "绝密", 8, "用户密级"),
    CONFIDENTIAL_USER("3", "机密", 8, "用户密级"),
    SECRET_USER("2", "秘密", 8, "用户密级"),
    INTERNAL_USER("1", "内部", 8, "用户密级"),
    PUBLIC_USER("0", "公开", 8, "用户密级"),

    // 行政归属
    COUNTY("2", "县属", 9, "行政归属"),
    MUNICIPAL("3", "市属", 9, "行政归属"),
    PROVINCIAL("4", "省属", 9, "行政归属"),
    CENTRAL("5", "中央", 9, "行政归属"),

    // 用户状态
    U_STATUS_NORMAL("1", "正常", 1001, "用户状态"),

    // 终端授权方式0手动  1自动
    AUTOMATIC_AUTHORIZATION("1", "自动授权", 10, "终端授权方式"),
    MANUAL_AUTHORIZATION("0", "手动授权", 10, "终端授权方式"),

    // 是否支持终端自动审核
    REVIEW_HOST_Y("1", "支持", 1002, "是否支持终端激活自动审核-支持"),
    REVIEW_HOST_N("0", "不支持", 1002, "是否支持终端激活自动审核-不支持"),
    ;
    // 成员变量
    private final String dictId;
    private final String dictName;
    private final int dictType;
    private final String dictTypeDes;

    public int getDictIdForInt(){
        return Integer.parseInt(this.dictId);
    }
}
