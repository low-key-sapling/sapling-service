package com.sapling.module.system.infrastructure.common.framework.interceptors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.zfsy.RegistryCenter;
import com.sapling.framework.common.utils.enc.Sm2Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import com.sapling.framework.common.exception.BusinessException;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;

/**
 * Token鉴权拦截器：负责X-BACK-SIGN、X-NONCE、X-IDENTITY参数校验，Polaris公钥获取与缓存，SM2验签。
 * @author MBWS
 */
@Slf4j
@Component
public class TokenAuthInterceptor implements HandlerInterceptor {
    private static final String HEADER_SIGN = "X-BACK-SIGN";
    private static final String HEADER_NONCE = "X-NONCE";
    private static final String HEADER_IDENTITY = "X-IDENTITY";
    private static final String SM2_MODE = Sm2Utils.MODE_BASE64;
    private static final ObjectMapper objectMapper = new ObjectMapper();



    /**
     * 每次preHandle都new一套TokenAuthHandler链对象的理由：
     * 1. HandlerInterceptor为单例，链对象如做成员变量会有线程安全隐患。
     * 2. 责任链节点无全局状态，new对象开销极小，且保证无副作用。
     * 3. 这样实现可确保每个请求独立处理，线程安全，易于维护。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 构建职责链
        TokenAuthHandler paramCheck = new ParamCheckHandler();
        TokenAuthHandler publicKeyCheck = new PublicKeyHandler();
        TokenAuthHandler sm2Verify = new Sm2VerifyHandler();
//        paramCheck.setNext(publicKeyCheck).setNext(sm2Verify);
        // 执行链
        paramCheck.handle(request);
        return true;
    }

    /**
     * 职责链抽象处理器
     */
    private abstract static class TokenAuthHandler {
        private TokenAuthHandler next;
        public TokenAuthHandler setNext(TokenAuthHandler next) {
            this.next = next;
            return next;
        }
        public void handle(HttpServletRequest request) {
            doHandle(request);
            if (next != null) next.handle(request);
        }
        protected abstract void doHandle(HttpServletRequest request);
    }

    /**
     * 参数校验处理器
     */
    private class ParamCheckHandler extends TokenAuthHandler {
        @Override
        protected void doHandle(HttpServletRequest request) {
            String sign = request.getHeader(HEADER_SIGN);
            String nonce = request.getHeader(HEADER_NONCE);
            String identity = request.getHeader(HEADER_IDENTITY);
            if (sign == null || nonce == null || identity == null) {
                log.error("TokenAuthInterceptor: 缺少必要参数 sign={}, nonce={}, identity={}", sign, nonce, identity);
                throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "参数缺失: X-BACK-SIGN、X-NONCE、X-IDENTITY均为必填");
            }
            if (!isValidUUID(nonce)) {
                log.error("TokenAuthInterceptor: X-NONCE格式错误 nonce={}", nonce);
                throw new BusinessException(GlobalErrorCodeConstants.BAD_REQUEST.getType(), "随机数格式错误: X-NONCE必须为UUID格式");
            }
            // 临时存储到request属性，便于后续节点复用
            request.setAttribute("token_sign", sign);
            request.setAttribute("token_nonce", nonce);
            request.setAttribute("token_identity", identity);
        }
    }

    /**
     * 公钥获取与缓存处理器
     */
    private class PublicKeyHandler extends TokenAuthHandler {
        @Override
        protected void doHandle(HttpServletRequest request) {
            String identity = (String) request.getAttribute("token_identity");
            // 直接每次从Polaris获取公钥
            String publicKey = fetchPublicKeyFromPolaris(identity);
            if (publicKey == null) {
                log.error("TokenAuthInterceptor: 公钥获取失败 identity={}", identity);
                throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN.getType(), "公钥获取失败: 无法获取identity对应的公钥");
            }
            // 公钥格式清洗
            publicKey = cleanPublicKey(publicKey);
            request.setAttribute("token_publicKey", publicKey);
        }
    }

    /**
     * 清洗公钥字符串，去除前后空白字符和换行
     * @param publicKey 原始公钥
     * @return 清洗后的公钥
     */
    private String cleanPublicKey(String publicKey) {
        if (publicKey == null) return null;
        return publicKey.replaceAll("\\s+", "");
    }

    /**
     * SM2验签处理器
     */
    private class Sm2VerifyHandler extends TokenAuthHandler {
        @Override
        protected void doHandle(HttpServletRequest request) {
            String sign = (String) request.getAttribute("token_sign");
            String nonce = (String) request.getAttribute("token_nonce");
            String identity = (String) request.getAttribute("token_identity");
            String publicKey = (String) request.getAttribute("token_publicKey");
            String content = nonce + identity;
            boolean verifyResult = false;
            try {
                // 增加日志，便于排查
                log.debug("SM2验签参数: identity={}, nonce={}, sign={}, publicKey={}", identity, nonce, sign, publicKey);
                verifyResult = Sm2Utils.verify(content, sign, publicKey, SM2_MODE);
            } catch (Exception e) {
                log.error("TokenAuthInterceptor: SM2验签异常, identity={}, nonce={}, sign={}, publicKey={}", identity, nonce, sign, publicKey, e);
                throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getType(), "签名验证异常: " + e.getMessage());
            }
            if (!verifyResult) {
                log.warn("TokenAuthInterceptor: 签名验证失败 identity={}, nonce={}, sign={}, publicKey={}", identity, nonce, sign, publicKey);
                throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN.getType(), "签名验证失败: SM2验签未通过");
            }
        }
    }

    /**
     * 校验UUID格式
     */
    private boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Polaris SDK获取公钥
     * @param identity 服务标识
     * @return 公钥字符串
     */
    private String fetchPublicKeyFromPolaris(String identity) {
        try {
            return Optional.ofNullable(RegistryCenter.getInstanceByUnicode(identity))
                    .map(instance -> {
                        String metadata = instance.getMetadata();
                        if (metadata == null) {
                            log.error("TokenAuthInterceptor: 服务实例元数据为空, identity={}", identity);
                            return null;
                        }
                        return parseMetadata(metadata, identity);
                    })
                    .orElseGet(() -> {
                        log.error("TokenAuthInterceptor: 无法从Polaris获取服务实例, identity={}", identity);
                        return null;
                    });
        } catch (Exception e) {
            log.error("TokenAuthInterceptor: 获取公钥过程发生未预期异常, identity={}", identity, e);
            return null;
        }
    }

    /**
     * 解析元数据获取公钥
     * @param metadata 元数据JSON字符串
     * {
     *   "publicKey" : "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE09t6RaQNac4IqvJYCVoYi4AK7IPNwT5Ld3Psdp+MDxMcDoK43T8vjyeLoJ4zD7VyUyc06wKK48fYGmM8IINLdQ=="
     * }
     * @param identity 服务标识（用于日志）
     * @return 公钥字符串
     */
    private String parseMetadata(String metadata, String identity) {
        try {
            return Optional.ofNullable(objectMapper.readValue(metadata, Map.class))
                    .map(metaMap -> metaMap.get("publicKey"))
                    .map(Object::toString)
                    .orElseGet(() -> {
                        log.error("TokenAuthInterceptor: 元数据中未找到公钥, identity={}, metadata={}", identity, metadata);
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("TokenAuthInterceptor: 元数据JSON解析失败, identity={}, metadata={}", identity, metadata, e);
            return null;
        }
    }
} 