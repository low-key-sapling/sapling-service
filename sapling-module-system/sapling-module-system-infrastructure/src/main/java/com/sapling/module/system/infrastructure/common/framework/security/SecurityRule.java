package com.sapling.module.system.infrastructure.common.framework.security;

import java.util.Optional;

/**
 * 安全检测规则接口，每个实现类负责一条或多条检测逻辑
 * @return Optional<RuleMatchResult>：如果检测匹配，返回包含规则信息的结果，否则返回 Optional.empty()
 */
public interface SecurityRule {
    Optional<RuleMatchResult> match(RequestContext context);
} 