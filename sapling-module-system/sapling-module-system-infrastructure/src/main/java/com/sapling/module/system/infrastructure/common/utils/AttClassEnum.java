package com.sapling.module.system.infrastructure.common.utils;


public enum AttClassEnum {
    APT("APT", 1),
    APT_TROJAN("窃密木马", 1),
    FISHING("钓鱼", 1),
    FISHING_WEB("钓鱼网站", 1),
    TROJAN("木马", 2),
    BANING_TROJAN("网银木马", 2),
    ATT_CLASS_ENUM("病毒", 3),
    SERIOUS("僵尸网络", 4),
    SCR_ICT("蠕虫", 5),
    SPYWARE("间谍软件", 6),
    MINING_TRO("挖矿", 7),
    RANSOMWARE("勒索", 8),
    BACKDOOR("后门程序", 9),
    OTHERS("其他", 99),
    HACKER_TOOLS("黑客工具", 99),
    ;
    private final String key;
    private final int value;

    AttClassEnum(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public static Integer getAttClassNum(String key) {
        for (AttClassEnum e : AttClassEnum.values()) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return 99;
    }

}