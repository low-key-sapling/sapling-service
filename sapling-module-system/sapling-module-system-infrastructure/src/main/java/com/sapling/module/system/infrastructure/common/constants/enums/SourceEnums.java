package com.sapling.module.system.infrastructure.common.constants.enums;

public enum SourceEnums {
    MBWS("mbws"), ZJG("zjg");

    private String value;

    SourceEnums(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
