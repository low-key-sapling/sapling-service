package com.sapling.module.system.infrastructure.common.framework.security;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 基于一组正则表达式进行检查的规则实现
 */
public class PatternSecurityRule implements SecurityRule {

    private final String ruleName;
    private final String description;
    private final List<Pattern> patterns;
    private final RuleTarget target;

    /**
     * @param ruleName    规则唯一名称
     * @param description 规则说明
     * @param patterns    要匹配的正则列表
     * @param target      检测目标：URI、PARAM_NAME、PARAM_VALUE、HEADER
     */
    public PatternSecurityRule(String ruleName, String description, List<Pattern> patterns, RuleTarget target) {
        this.ruleName = ruleName;
        this.description = description;
        this.patterns = patterns;
        this.target = target;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getDescription() {
        return description;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public RuleTarget getTarget() {
        return target;
    }

    @Override
    public Optional<RuleMatchResult> match(RequestContext context) {
        switch (target) {
            case URI:
                return matchString(context.getUri());
            case PARAM_NAME:
                return matchParamNames(context);
            case PARAM_VALUE:
                return matchParamValues(context);
            case HEADER:
                return matchHeaders(context);
            default:
                return Optional.empty();
        }
    }

    private Optional<RuleMatchResult> matchString(String input) {
        if (input == null || input.isEmpty()) {
            return Optional.empty();
        }
        for (Pattern p : patterns) {
            java.util.regex.Matcher m = p.matcher(input);
            if (m.find()) {
                return Optional.of(new RuleMatchResult(ruleName, description, m.group()));
            }
        }
        return Optional.empty();
    }

    private Optional<RuleMatchResult> matchParamNames(RequestContext ctx) {
        for (String paramName : ctx.getParameterMap().keySet()) {
            Optional<RuleMatchResult> result = matchString(paramName);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    private Optional<RuleMatchResult> matchParamValues(RequestContext ctx) {
        for (String[] values : ctx.getParameterMap().values()) {
            for (String val : values) {
                Optional<RuleMatchResult> result = matchString(val);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

    private Optional<RuleMatchResult> matchHeaders(RequestContext ctx) {
        for (String headerVal : ctx.getHeaders().values()) {
            Optional<RuleMatchResult> result = matchString(headerVal);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    public enum RuleTarget {
        URI, PARAM_NAME, PARAM_VALUE, HEADER
    }
} 