package com.zyg.guns.core.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 信息摘要算法
 * Created by wyq on 2016/6/15.
 */
public class MD5Util {
    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);
    //默认字符集
    private static final String CHARSET = "UTF-8";

    /**
     * 获取MD5摘要
     *
     * @param bytes
     * @return byte[]
     */
    public static byte[] getMD5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取MD5摘要
     *
     * @param str
     * @return String
     */
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(CHARSET));
            return HexBin.encode(bytes);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 校验MD5摘要
     *
     * @param cleartext
     * @param ciphertext
     * @return Boolean
     */
    public static Boolean validateMD5(byte[] cleartext, byte[] ciphertext) {
        try {
            String clear = new String(cleartext, CHARSET);
            String cipher = new String(ciphertext, CHARSET);
            return validateMD5(clear, cipher);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 校验MD5摘要
     *
     * @param cleartext
     * @param ciphertext
     * @return Boolean
     */
    public static Boolean validateMD5(String cleartext, String ciphertext) {
        String str = getMD5(cleartext);
        if (str != null && str.equals(ciphertext)) {
            return true;
        } else {
            return false;
        }
    }
}
