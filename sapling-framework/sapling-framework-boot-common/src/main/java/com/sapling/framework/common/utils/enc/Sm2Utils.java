package com.sapling.framework.common.utils.enc;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.util.encoders.Hex;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mbws
 * @version 1.0
 * @date 2024/11/10 11:33
 * @des
 */
public class Sm2Utils {
    /**
     * 类型
     */
    public static final String ENCRYPT_TYPE = "SM2";

    /**
     * 获取公钥的key
     */
    public static final String PUBLIC_KEY = "SM2PublicKey";

    /**
     * 获取私钥的key
     */
    public static final String PRIVATE_KEY = "SM2PrivateKey";

    public static final String MODE_BCD = "BCD";
    public static final String MODE_HEX = "HEX";
    public static final String MODE_BASE64 = "BASE64";

    /**
     * 获取公私钥-请获取一次后保存公私钥使用
     *
     * @return
     */
    public static Map<String, String> generateKeyPairBase64() {
        try {
            KeyPair pair = SecureUtil.generateKeyPair(ENCRYPT_TYPE);
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            // 获取 公钥和私钥 的 编码格式（通过该 编码格式 可以反过来 生成公钥和私钥对象）
            byte[] pubEncBytes = publicKey.getEncoded();
            byte[] priEncBytes = privateKey.getEncoded();

            // 把 公钥和私钥 的 编码格式 转换为 Base64文本 方便保存
            String pubEncBase64 = Base64.encode(pubEncBytes);
            String priEncBase64 = Base64.encode(priEncBytes);

            Map<String, String> map = new HashMap<String, String>(2);
            map.put(PUBLIC_KEY, pubEncBase64);
            map.put(PRIVATE_KEY, priEncBase64);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> generateKeyPairHex() {
        try {
            KeyPair pair = SecureUtil.generateKeyPair(ENCRYPT_TYPE);
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            // 获取 公钥和私钥 的 编码格式（通过该 编码格式 可以反过来 生成公钥和私钥对象）
            byte[] pubEncBytes = publicKey.getEncoded();
            byte[] priEncBytes = privateKey.getEncoded();

            // 把 公钥和私钥 的 编码格式 转换为 Hash文本 方便保存
            String pubEncHex = Hex.toHexString(pubEncBytes);
            String priEncHex = Hex.toHexString(priEncBytes);

            Map<String, String> map = new HashMap<String, String>(2);
            map.put(PUBLIC_KEY, pubEncHex);
            map.put(PRIVATE_KEY, priEncHex);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String str, String pubKey, String mode) {
        byte[] keyBytes = Validator.isHex(pubKey) ? HexUtil.decodeHex(pubKey) : Base64.decode(pubKey);
        SM2 sm2 = SmUtil.sm2(null, keyBytes);
        if (mode.equals(MODE_BCD)) {
            return sm2.encryptBcd(str, KeyType.PublicKey);
        }
        if (mode.equals(MODE_HEX)) {
            return sm2.encryptHex(str, KeyType.PublicKey);
        }
        return sm2.encryptBase64(str, KeyType.PublicKey);
    }

    public static String decrypt(String str, String pribKey, String mode) {
        byte[] keyBytes = Validator.isHex(pribKey) ? HexUtil.decodeHex(pribKey) : Base64.decode(pribKey);
        SM2 sm2 = SmUtil.sm2(keyBytes, null);
        if (mode.equals(MODE_BCD)) {
            return sm2.decryptStrFromBcd(str, KeyType.PrivateKey);
        }
        return sm2.decryptStr(str, KeyType.PrivateKey);
    }

    /**
     * SM2签名
     *
     * @param content 待签名内容
     * @param privateKey 私钥
     * @param mode 签名模式（BASE64/HEX）
     * @return 签名结果
     */
    public static String sign(String content, String privateKey, String mode) {
        try {
            byte[] keyBytes = Validator.isHex(privateKey) ? HexUtil.decodeHex(privateKey) : Base64.decode(privateKey);
            SM2 sm2 = SmUtil.sm2(keyBytes, null);
            byte[] sign = sm2.sign(content.getBytes());
            
            if (MODE_HEX.equals(mode)) {
                return HexUtil.encodeHexStr(sign);
            }
            return Base64.encode(sign);
        } catch (Exception e) {
            throw new RuntimeException("SM2签名失败", e);
        }
    }

    /**
     * SM2验签
     *
     * @param content 原始内容
     * @param sign 签名值
     * @param publicKey 公钥
     * @param mode 签名模式（BASE64/HEX）
     * @return 验签结果
     */
    public static boolean verify(String content, String sign, String publicKey, String mode) {
        try {
            if (ObjectUtil.isEmpty(content)) {
                return false;
            }

            byte[] keyBytes = Validator.isHex(publicKey) ? HexUtil.decodeHex(publicKey) : Base64.decode(publicKey);
            SM2 sm2 = SmUtil.sm2(null, keyBytes);
            
            byte[] signBytes;
            if (MODE_HEX.equals(mode)) {
                signBytes = HexUtil.decodeHex(sign);
            } else {
                signBytes = Base64.decode(sign);
            }
            
            return sm2.verify(content.getBytes(), signBytes);
        } catch (Exception e) {
            throw new RuntimeException("SM2验签失败", e);
        }
    }

}
