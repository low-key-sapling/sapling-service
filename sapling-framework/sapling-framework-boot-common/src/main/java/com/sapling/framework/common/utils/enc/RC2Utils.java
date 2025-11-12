package com.sapling.framework.common.utils.enc;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author mbws
 * @version 1.0
 * @date 2024/11/15 22:05
 * @des
 */
public class RC2Utils {
    public static byte[] generateKey() {
        // 随机生成密钥
        return SecureUtil.generateKey(SymmetricAlgorithm.RC2.getValue()).getEncoded();
    }

    public static String generateKeyHex() {
        byte[] key = generateKey();
        return Hex.toHexString(key);
    }

    public static String generateKeyBase64() {
        byte[] key = generateKey();
        return Base64.encode(key);
    }

    public static String encrypt(String str, String key) {
        byte[] keyBytes = Validator.isHex(key) ? HexUtil.decodeHex(key) : Base64.decode(key);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.RC2, keyBytes);
        return Validator.isHex(key) ? aes.encryptHex(str, "UTF-8") : aes.encryptBase64(str, "UTF-8");
    }

    public static String decrypt(String str, String key) {
        byte[] keyBytes = Validator.isHex(key) ? HexUtil.decodeHex(key) : Base64.decode(key);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.RC2, keyBytes);
        return aes.decryptStr(str);
    }

    //public static void main(String[] args) {
    //    String key=generateKeyBase64();
    //    System.out.println(key);
    //    //key="2691b31aa11bee4b62c05537aa67558a";
    //    System.out.println(key);
    //    String str="{\n" +
    //            "    \"username\":\"admin\",\n" +
    //            "    \"password\":\"123\"\n" +
    //            "}";
    //    String encrypt=encrypt(str, key);
    //    System.out.println(encrypt);
    //    String str2=decrypt(encrypt, key);
    //    System.out.println(str2);
    //}
}
