package com.sapling.module.system.infrastructure.common.framework.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * RuleEngine: 维护所有可用规则，按顺序检测每次请求
 */
@Slf4j
@Service
public class RuleEngine {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final List<SecurityRule> rules = new ArrayList<>();

    @Value("${endpoint.security.enabled:true}")
    private boolean enabled;

    @Value("${endpoint.security.patterns.sql-injection}")
    private String sqlInjectionPattern;

    @Value("${endpoint.security.patterns.xss}")
    private String xssPattern;

    @Value("${endpoint.security.patterns.path-traversal}")
    private String pathTraversalPattern;

    @Value("${endpoint.security.patterns.command-injection}")
    private String commandInjectionPattern;

    @Value("${endpoint.security.patterns.file-upload}")
    private String fileUploadPattern;

    @Value("${endpoint.security.patterns.xxe}")
    private String xxePattern;

    @Value("${endpoint.security.patterns.template-injection}")
    private String templateInjectionPattern;

    @Value("${endpoint.security.patterns.deserialization}")
    private String deserializationPattern;

    @PostConstruct
    public void init() {
        if (!enabled) {
            log.info("安全规则引擎已禁用");
            return;
        }

        // 初始化规则
        initRules();
        log.info("安全规则引擎初始化完成，共加载 {} 条规则", rules.size());
    }

    private void initRules() {
        try {
            // SQL注入检测规则
            PatternSecurityRule sqlRule = new PatternSecurityRule(
                "SQL_INJECTION",
                "检测 SQL 注入关键字",
                Arrays.asList(Pattern.compile(sqlInjectionPattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // XSS检测规则
            PatternSecurityRule xssRule = new PatternSecurityRule(
                "XSS",
                "检测 XSS 脚本标签",
                Arrays.asList(Pattern.compile(xssPattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // 路径穿越检测规则
            PatternSecurityRule ptRule = new PatternSecurityRule(
                "PATH_TRAVERSAL",
                "检测路径穿越",
                Arrays.asList(Pattern.compile(pathTraversalPattern)),
                PatternSecurityRule.RuleTarget.URI
            );

            // 命令注入检测规则
            PatternSecurityRule cmdRule = new PatternSecurityRule(
                "COMMAND_INJECTION",
                "检测命令注入",
                Arrays.asList(Pattern.compile(commandInjectionPattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // 危险文件上传检测规则
            PatternSecurityRule fileRule = new PatternSecurityRule(
                "DANGEROUS_FILE",
                "检测危险文件扩展名",
                Arrays.asList(Pattern.compile(fileUploadPattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // XML外部实体注入检测规则
            PatternSecurityRule xxeRule = new PatternSecurityRule(
                "XXE",
                "检测 XML 外部实体注入",
                Arrays.asList(Pattern.compile(xxePattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // 模板注入检测规则
            PatternSecurityRule templateRule = new PatternSecurityRule(
                "TEMPLATE_INJECTION",
                "检测模板注入",
                Arrays.asList(Pattern.compile(templateInjectionPattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // 反序列化检测规则
            PatternSecurityRule deserializationRule = new PatternSecurityRule(
                "DESERIALIZATION",
                "检测反序列化攻击",
                Arrays.asList(Pattern.compile(deserializationPattern)),
                PatternSecurityRule.RuleTarget.PARAM_VALUE
            );

            // 添加所有规则
            rules.add(sqlRule);
            rules.add(xssRule);
            rules.add(ptRule);
            rules.add(cmdRule);
            rules.add(fileRule);
            rules.add(xxeRule);
            rules.add(templateRule);
            rules.add(deserializationRule);
        } catch (Exception e) {
            log.error("初始化安全规则失败", e);
            throw e;
        }
    }

    /**
     * 对一次请求执行所有规则检测
     * @return Optional<RuleMatchResult>：如果任意规则匹配，则返回第一个匹配的结果，否则返回 empty
     */
    public Optional<RuleMatchResult> check(HttpServletRequest request) {
        if (!enabled) {
            return Optional.empty();
        }

        try {
            RequestContext ctx = new RequestContext(request);
            
            // 1. 检查请求参数
            for (SecurityRule rule : rules) {
                Optional<RuleMatchResult> match = rule.match(ctx);
                if (match.isPresent()) {
                    log.warn("安全规则 [{}] 命中：IP={}，URI={}，匹配内容={}", 
                        match.get().getRuleName(),
                        ctx.getRemoteAddr(),
                        ctx.getUri(),
                        match.get().getMatchedContent());
                    return match;
                }
            }

            // 2. 检查请求体（如果是 JSON）
            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                String body;
                if (request instanceof ContentCachingRequestWrapper) {
                    ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                    body = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                } else {
                    body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                }
                
                if (body != null && !body.isEmpty()) {
                    log.debug("检查请求体: {}", body);
                    try {
                        JsonNode jsonNode = objectMapper.readTree(body);
                        Optional<RuleMatchResult> bodyMatch = checkJsonNode(jsonNode);
                        if (bodyMatch.isPresent()) {
                            log.warn("安全规则 [{}] 命中：IP={}，URI={}，匹配内容={}", 
                                bodyMatch.get().getRuleName(),
                                ctx.getRemoteAddr(),
                                ctx.getUri(),
                                bodyMatch.get().getMatchedContent());
                            return bodyMatch;
                        }
                    } catch (Exception e) {
                        log.error("解析 JSON 请求体失败", e);
                    }
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("安全规则检查异常", e);
            throw new SecurityException("安全规则检查失败", e);
        }
    }

    /**
     * 递归检查 JSON 节点中的所有值
     */
    private Optional<RuleMatchResult> checkJsonNode(JsonNode node) {
        if (node.isTextual()) {
            String value = node.asText();
            log.debug("检查 JSON 文本值: {}", value);
            for (SecurityRule rule : rules) {
                if (rule instanceof PatternSecurityRule) {
                    PatternSecurityRule patternRule = (PatternSecurityRule) rule;
                    for (Pattern pattern : patternRule.getPatterns()) {
                        if (pattern.matcher(value).find()) {
                            log.warn("JSON 值匹配安全规则 [{}]: {}", patternRule.getRuleName(), value);
                            return Optional.of(new RuleMatchResult(
                                patternRule.getRuleName(),
                                patternRule.getDescription(),
                                value
                            ));
                        }
                    }
                }
            }
        } else if (node.isObject()) {
            for (JsonNode child : node) {
                Optional<RuleMatchResult> match = checkJsonNode(child);
                if (match.isPresent()) {
                    return match;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                Optional<RuleMatchResult> match = checkJsonNode(child);
                if (match.isPresent()) {
                    return match;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 动态刷新规则
     */
    public void reloadRules(List<SecurityRule> newRules) {
        rules.clear();
        rules.addAll(newRules);
        log.info("安全规则已重新加载，当前规则数：{}", rules.size());
    }
} 