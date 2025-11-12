package com.sapling.framework.common.utils.enc;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapling.framework.common.exception.BasicException;
import com.sapling.framework.common.exception.enums.AppHttpStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.*;

/**
 * SM2签名工具类
 * 负责HTTP请求参数的扁平化、排序、拼接以及SM2签名。
 * 通过接口注入私钥提供者，避免依赖上层业务。
 */
@Slf4j
public class Sm2SignUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * -- SETTER --
     *  注入私钥提供者
     *
     */
    @Setter
    private static PrivateKeyProvider privateKeyProvider;

    /**
     * 获取SM2私钥
     * 
     * @return 返回有效的SM2私钥字符串
     * @throws BasicException 当私钥提供者未注入或私钥未初始化时抛出
     *                       异常状态码：BIZ_BACK_VERIFY_SIGNATURE_FAILED
     */
    private static String getPrivateKey() {
        /**
         * 检查私钥提供者是否已注入
         * 若未注入则记录错误日志并抛出异常
         */
        if (privateKeyProvider == null) {
            log.error("SM2私钥提供者未注入，无法进行签名。");
            throw new BasicException(AppHttpStatus.BIZ_BACK_VERIFY_SIGNATURE_FAILED.getStatus(), "SM2私钥提供者未注入");
        }
        String privateKey = privateKeyProvider.getSm2PrivateKey();
        /**
         * 验证获取的私钥有效性
         * 若私钥为空白则记录错误日志并抛出异常
         */
        if (StringUtils.isBlank(privateKey)) {
            log.error("SM2私钥未初始化或获取失败，无法进行签名。");
            throw new BasicException(AppHttpStatus.BIZ_BACK_VERIFY_SIGNATURE_FAILED.getStatus(), "SM2私钥未初始化或获取失败");
        }
        return privateKey;
    }

    /**
     * 对字符串内容进行SM2签名（重载）
     *
     * @param content 待签名内容字符串
     * @return Base64编码的SM2签名字符串
     * @throws BasicException 如果私钥获取失败或签名过程中出现异常
     */
    public static String signRequest(String content) {
        try {
            // 签名
            return sign(content, getPrivateKey());
        } catch (Exception e) {
            log.error("SM2签名失败: {}", ExceptionUtils.getStackTrace(e));
            throw new BasicException(AppHttpStatus.BIZ_BACK_VERIFY_SIGNATURE_FAILED.getStatus(), "SM2签名失败");
        }
    }

    /**
     * SM2签名
     *
     * @param content    待签名内容
     * @param privateKey 私钥
     * @return 签名结果
     */
    public static String sign(String content, String privateKey) {
        byte[] keyBytes = Validator.isHex(privateKey) ? HexUtil.decodeHex(privateKey) : cn.hutool.core.codec.Base64.decode(privateKey);
        SM2 sm2 = SmUtil.sm2(keyBytes, null);
        byte[] sign = sm2.sign(content.getBytes());

        return cn.hutool.core.codec.Base64.encode(sign);
    }

    /**
     * 对请求参数进行SM2签名（Map版本）
     *
     * @param paramsMap 包含URL参数、Header参数和Body参数的Map
     * @return Base64编码的SM2签名字符串
     * @throws BasicException 如果私钥获取失败或签名过程中出现异常
     */
    public static String signRequest(Map<String, Object> paramsMap) {
        String privateKey = getPrivateKey();
        String signContent = buildSignContent(paramsMap);
        log.debug("签名原文：{}", signContent);
        try {
            // 签名
            return sign(signContent, privateKey);
        } catch (Exception e) {
            log.error("SM2签名失败: {}", e.getMessage());
            throw new BasicException(AppHttpStatus.BIZ_BACK_VERIFY_SIGNATURE_FAILED.getStatus(), "SM2签名失败");
        }
    }

    private static String buildSignContent(Map<String, Object> paramsMap) {
        Map<String, String> flatParams = flattenParams(paramsMap);
        List<String> keys = new ArrayList<>(flatParams.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(flatParams.get(key));
        }
        return sb.toString();
    }

    private static Map<String, String> flattenParams(Map<String, Object> paramsMap) {
        Map<String, String> flatParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenMap(key, (Map<?, ?>) value, flatParams);
            } else if (value instanceof Collection) {
                flattenCollection(key, (Collection<?>) value, flatParams);
            } else if (value instanceof String) {
                try {
                    JsonNode jsonNode = OBJECT_MAPPER.readTree((String) value);
                    if (jsonNode.isObject() || jsonNode.isArray()) {
                        flattenJsonNode(key, jsonNode, flatParams);
                    } else {
                        flatParams.put(key, String.valueOf(value));
                    }
                } catch (IOException e) {
                    flatParams.put(key, String.valueOf(value));
                }
            } else {
                flatParams.put(key, String.valueOf(value));
            }
        }
        return flatParams;
    }

    private static void flattenMap(String prefix, Map<?, ?> map, Map<String, String> flatParams) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String newKey = prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenMap(newKey, (Map<?, ?>) value, flatParams);
            } else if (value instanceof Collection) {
                flattenCollection(newKey, (Collection<?>) value, flatParams);
            } else {
                flatParams.put(newKey, String.valueOf(value));
            }
        }
    }

    private static void flattenCollection(String prefix, Collection<?> collection, Map<String, String> flatParams) {
        int index = 0;
        for (Object item : collection) {
            String newKey = prefix + "[" + index + "]";
            if (item instanceof Map) {
                flattenMap(newKey, (Map<?, ?>) item, flatParams);
            } else if (item instanceof Collection) {
                flattenCollection(newKey, (Collection<?>) item, flatParams);
            } else {
                flatParams.put(newKey, String.valueOf(item));
            }
            index++;
        }
    }

    private static void flattenJsonNode(String prefix, JsonNode jsonNode, Map<String, String> flatParams) {
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String newKey = prefix + "." + field.getKey();
                flattenJsonNode(newKey, field.getValue(), flatParams);
            }
        } else if (jsonNode.isArray()) {
            for (int i = 0; i < jsonNode.size(); i++) {
                String newKey = prefix + "[" + i + "]";
                flattenJsonNode(newKey, jsonNode.get(i), flatParams);
            }
        } else {
            flatParams.put(prefix, jsonNode.asText());
        }
    }
} 