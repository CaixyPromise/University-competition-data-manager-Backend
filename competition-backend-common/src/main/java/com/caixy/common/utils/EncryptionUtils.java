package com.caixy.common.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtils
{
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String SALT = "CAIXYPROMISE";

    public static String encodePassword(String rawPassword)
    {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matches(String originPassword, String hashPassword)
    {
        return passwordEncoder.matches(originPassword, hashPassword);
    }

    public static String makeUserKey(String content)
    {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(SALT)
                .append(".")
                .append(content)
                .append(".")
                .append(System.currentTimeMillis())
                .append(".")
                .append(Arrays.toString(RandomUtil.randomInts(5)));
        return md5.digestHex(stringBuffer.toString());
    }

    public static String decodePassword(String encryptedText, byte[] key, byte[] iv) throws Exception
    {
        // 初始化Cipher对象用于AES解密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // 将Base64编码的加密字符串转换回字节并解密
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

        // 将解密后的字节转换成字符串
        String decryptedString = new String(original, StandardCharsets.UTF_8);

        // 提取密码部分（假设密码在时间戳和nonce之间）
        // 格式：timestamp.plainText.nonce
        String[] parts = decryptedString.split("\\.");
        if (parts.length >= 3)
        {
            return parts[1]; // 返回密码部分
        }
        else
        {
            throw new IllegalArgumentException("Invalid encrypted text format");
        }
    }
}
