package com.sapling.framework.common.utils.enc;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 * <p>
 */
@Slf4j
public class JasyptUtil {
    /**
     * JasyptKey
     */
    private static String jasyptKey = "GeZmxIduyAT0aoYe";
    // 创建BasicTextEncryptor实例用于密码加密和解密
    private static final BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
    // 创建BasicPasswordEncryptor实例用于密码加密和验证
    private static final BasicPasswordEncryptor basicPasswordEncryptor = new BasicPasswordEncryptor();
    // 创建PooledPBEStringEncryptor并配置加密器，使用池化技术以支持多线程加密
    private static final PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();

    public static void setJasyptKey(String key) {
        JasyptUtil.jasyptKey = key;
    }

    static {
        //基础加解密实例参数初始化
        basicTextEncryptor.setPassword(jasyptKey);

        //多线程加解密实例参数初始化
        // 设置线程池大小为6，即同时最多有6个线程执行加密操作
        pooledPBEStringEncryptor.setPoolSize(6);
        // 设置加密使用的密码
        pooledPBEStringEncryptor.setPassword(jasyptKey);
        // 设置加密算法
        pooledPBEStringEncryptor.setAlgorithm("PBEWithMD5AndTripleDES");

    }

    /**
     * @param encryptText 密文
     * @return java.lang.String 解密后文本
     * @author zhongfu
     * @Description 项目中使用JasyptUtil通用解密方法
     * @date 2024/6/6 9:46
     */
    public static String jasyptDecrypt4EndPoint(String encryptText) {
        //以ENCRYPTOR_PREFIX常量值开头的便是密文需要解密
        //否则认为是明文不再进行处理
        if (encryptText.startsWith("enc`")) {
            encryptText = encryptText.replace("enc`", "");
            encryptText = JasyptUtil.basicDecrypted(encryptText);
        }
        return encryptText;
    }

    /**
     * @param encryptText 待加密文本
     * @return java.lang.String 加密后文本
     * @author zhongfu
     * @Description 使用BasicTextEncryptor对文本进行加密
     * @date 2024/6/5 17:24
     */
    public static String basicEncrypt(String encryptText) {
        try {
            // 加密文本信息
            return basicTextEncryptor.encrypt(encryptText);
        } catch (Exception e) {
            // 处理加密/解密过程中可能出现的异常
            log.error("Error during encryption: ", e);
        }
        return null;
    }

    /**
     * @param decryptedText 待解密文本
     * @return java.lang.String 解密后文本
     * @author zhongfu
     * @Description 使用BasicTextEncryptor对文本进行解密
     * @date 2024/6/5 17:24
     */
    public static String basicDecrypted(String decryptedText) {
        try {
            // 解密已加密的文本信息
            return basicTextEncryptor.decrypt(decryptedText);
        } catch (Exception e) {
            // 处理加密/解密过程中可能出现的异常
            log.error("Error during decryption: ", e);
        }
        return null;
    }

    /**
     * @param encryptText 待加密文本
     * @return java.lang.String 加密后文本
     * @author zhongfu
     * @Description 使用BasicPasswordEncryptor对文本进行加密
     * @date 2024/6/5 17:24
     */
    public static String basicPasswordEncrypt(String encryptText) {
        try {
            // 加密文本
            return basicPasswordEncryptor.encryptPassword(encryptText);
        } catch (Exception e) {
            // 处理加密/解密过程中可能出现的异常
            log.error("Error during encryption: ", e);
        }
        return null;
    }

    /**
     * @param plainPassword     明文(如前端输入密码)
     * @param encryptedPassword 密文(如数据库存储密码)
     * @return boolean 校验通过返回true否则返回false
     * @author zhongfu
     * @Description 使用BasicPasswordEncryptor实例对输入密码进行验证
     * @date 2024/6/5 17:45
     */
    private static boolean oneWayPasswordCheck(String plainPassword, String encryptedPassword) {
        // 检查密码 plainPassword 是否与加密后的密码匹配
        return basicPasswordEncryptor.checkPassword(plainPassword, encryptedPassword);
    }

    /**
     * @param encryptText 待加密文本
     * @return java.lang.String 加密后文本
     * @author zhongfu
     * @Description 使用PooledPBEStringEncryptor对文本进行加密
     * @date 2024/6/5 17:24
     */
    private static String multiThreadEncrypted(String encryptText) {
        // 加密明文字符串
        return pooledPBEStringEncryptor.encrypt(encryptText);
    }

    /**
     * @param decryptedText 加密文本
     * @return java.lang.String 解密后文本
     * @author zhongfu
     * @Description 使用PooledPBEStringEncryptor对文本进行解密
     * @date 2024/6/5 17:24
     */
    private static String multiThreadDecrypted(String decryptedText) {
        // 解密密文字符串
        return pooledPBEStringEncryptor.decrypt(decryptedText);
    }

    /**
     * 该示例演示如何改变加密算法。
     * 该方法不接受参数且无返回值。
     * 主要步骤包括创建加密器、设置密码和算法、加密数据以及解密数据。
     * <p>
     * 自定义地使用不同的算法进行加密解密
     */
    private static void changeAlgorithmExample() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        System.out.println("---------");
        // 设置加密器的密码
        //encryptor.setPassword(generateSecurePassword());
        encryptor.setPassword("rQIVmVRhL7Zr2Kmu");
        // 设置加密器使用的加密算法
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");

        // 使用加密器对文本进行加密
        String encryptedText = encryptor.encrypt("123ABCdef*");
        System.out.println("encryptedText:" + encryptedText);

        // 使用加密器对加密后的文本进行解密
        String decryptedText = encryptor.decrypt(encryptedText);
        System.out.println("decryptedText:" + decryptedText);
    }

    public static void main(String[] args) {
        final String s = basicEncrypt("nacos");
        System.out.println(s);
        System.out.println(basicDecrypted(s));

    }
}
    