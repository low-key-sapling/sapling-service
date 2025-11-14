package com.sapling.framework.common.utils.enc;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author mbws
 * @version 1.0
 * @date 2024/11/10 14:04
 * @des SM4 加密工具类
 */
public class Sm4Utils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String SM4_MODE_PADDING = "SM4/CBC/PKCS7Padding";

    /**
     * 生成SM4密钥
     */
    public static byte[] generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("SM4", BouncyCastleProvider.PROVIDER_NAME);
        kg.init(128, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    /**
     * 生成SM4密钥（Hex格式）
     */
    public static String generateKeyHex() throws Exception {
        byte[] key = generateKey();
        return Hex.toHexString(key);
    }

    /**
     * 生成SM4密钥（Base64格式）
     */
    public static String generateKeyBase64() throws Exception {
        byte[] key = generateKey();
        return Base64.encode(key);
    }

    /**
     * 生成IV（初始化向量）
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 生成IV（Hex格式）
     */
    public static String generateIVHex() {
        return Hex.toHexString(generateIV());
    }

    /**
     * 生成IV（Base64格式）
     */
    public static String generateIVBase64() {
        return Base64.encode(generateIV());
    }

    /**
     * 使用ECB模式加密（Hex格式）
     */
    public static String encryptHex(String str, String keyHex) {
        SecretKey secretKey = new SecretKeySpec(Hex.decode(keyHex), "SM4");
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey);
        return sm4.encryptHex(str);
    }

    /**
     * 使用ECB模式加密（Base64格式）
     */
    public static String encryptBase64(String str, String keyBase64) {
        SecretKey secretKey = new SecretKeySpec(Base64.decode(keyBase64), "SM4");
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey);
        return Base64.encode(sm4.encrypt(str));
    }

    /**
     * 使用ECB模式解密（Hex格式）
     */
    public static String decryptHex(String str, String keyHex) {
        SecretKey secretKey = new SecretKeySpec(Hex.decode(keyHex), "SM4");
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey);
        return sm4.decryptStr(str);
    }

    /**
     * 使用ECB模式解密（Base64格式）
     */
    public static String decryptBase64(String str, String keyBase64) {
        SecretKey secretKey = new SecretKeySpec(Base64.decode(keyBase64), "SM4");
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey);
        return sm4.decryptStr(str);
    }

    /**
     * 使用CBC模式加密（Hex格式）
     */
    public static String encryptHexCBC(String str, String keyHex, String ivHex) {
        SecretKey secretKey = new SecretKeySpec(Hex.decode(keyHex), "SM4");
        IvParameterSpec ivSpec = new IvParameterSpec(Hex.decode(ivHex));
        SymmetricCrypto sm4 = new SymmetricCrypto(SM4_MODE_PADDING, secretKey, ivSpec);
        return sm4.encryptHex(str);
    }

    /**
     * 使用CBC模式加密（Base64格式）
     */
    public static String encryptBase64CBC(String str, String keyBase64, String ivBase64) {
        SecretKey secretKey = new SecretKeySpec(Base64.decode(keyBase64), "SM4");
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.decode(ivBase64));
        SymmetricCrypto sm4 = new SymmetricCrypto(SM4_MODE_PADDING, secretKey, ivSpec);
        return Base64.encode(sm4.encrypt(str));
    }

    /**
     * 使用CBC模式解密（Hex格式）
     */
    public static String decryptHexCBC(String str, String keyHex, String ivHex) {
        SecretKey secretKey = new SecretKeySpec(Hex.decode(keyHex), "SM4");
        IvParameterSpec ivSpec = new IvParameterSpec(Hex.decode(ivHex));
        SymmetricCrypto sm4 = new SymmetricCrypto(SM4_MODE_PADDING, secretKey, ivSpec);
        return sm4.decryptStr(str);
    }

    /**
     * 使用CBC模式解密（Base64格式）
     */
    public static String decryptBase64CBC(String str, String keyBase64, String ivBase64) {
        SecretKey secretKey = new SecretKeySpec(Base64.decode(keyBase64), "SM4");
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.decode(ivBase64));
        SymmetricCrypto sm4 = new SymmetricCrypto(SM4_MODE_PADDING, secretKey, ivSpec);
        return sm4.decryptStr(str);
    }

    /**
     * 智能加密（自动判断密钥格式）
     */
    public static String encrypt(String str, String key) {
        byte[] keyBytes = Validator.isHex(key) ? HexUtil.decodeHex(key) : Base64.decode(key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "SM4");
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey);
        return Validator.isHex(key) ? sm4.encryptHex(str) : Base64.encode(sm4.encrypt(str));
    }

    /**
     * 智能解密（自动判断密钥格式）
     */
    public static String decrypt(String str, String key) {
        byte[] keyBytes = Validator.isHex(key) ? HexUtil.decodeHex(key) : Base64.decode(key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "SM4");
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey);
        return sm4.decryptStr(str);
    }
}
