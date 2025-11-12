package com.sapling.module.system.infrastructure.common.framework.security;

/**
 * 当某条规则匹配时，封装该次匹配的信息
 */
public class RuleMatchResult {
    private final String ruleName;       // 规则名称
    private final String description;    // 规则描述
    private final String matchedContent; // 匹配到的具体内容

    public RuleMatchResult(String ruleName, String description, String matchedContent) {
        this.ruleName = ruleName;
        this.description = description;
        this.matchedContent = matchedContent;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getDescription() {
        return description;
    }

    public String getMatchedContent() {
        return matchedContent;
    }
} 