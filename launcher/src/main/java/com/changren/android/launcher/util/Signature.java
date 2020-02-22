package com.changren.android.launcher.util;

import android.text.TextUtils;
import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Signature {

    private static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_KEY = "#@%^&";

    /**
     * 获取网络请求时需要的sign签名参数
     * 规则：
     * 首先传入接口访问参数及内容，然后对参数ASCII码值排序，
     * 再使用HmacSHA1算法加密得到字符串，再做Base64处理获得最终签名
     *
     * @param prefix    接口请求方式 + 接口地址（GET&index/index/test?）
     * @param key       HmacSHA1算法私钥
     * @param params    接口传入参数
     * @return          正式签名
     */
    public static String createSignature(String key, String prefix, Map<String, String> params) {
        try {
            //对参数ASCII码值排序
            String paramsStr = prefix + getParamsSerializeString(params);
            if (TextUtils.isEmpty(paramsStr)) {
                return "";
            }

            if (TextUtils.isEmpty(key)) {
                key = HMAC_KEY;
            }

            //1.制定使用HMAC_SHA1算法加密
            final Mac mac = Mac.getInstance(HMAC_SHA1);
            //2.根据给定的字节数组（私钥）构造一个密钥
            mac.init(new SecretKeySpec(key.getBytes(), HMAC_SHA1));
            //3.根据密钥加密给定字符串，并将返回的byte数组转化字符串
            //这一步不能少，否则会和PHP后台签名不一致
            String params_SHA1 = toHexString(mac.doFinal(paramsStr.getBytes()));
            //4.最后对加密字符串作Base64编码转换
            return Base64.encodeToString(params_SHA1.getBytes(), Base64.NO_WRAP);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 根据参数的ASCII码值排序
     * @param params 参数列表
     * @return 从小到大的参数列表组成的字符串
     */
    private static String getParamsSerializeString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        Set<String> keySetTry = new TreeSet<>(params.keySet());
        StringBuilder sb = new StringBuilder();
        List<String> keyList = new ArrayList<>(keySetTry);
        for (String key : keyList) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 将byte字节数组转化为16进制字符串
     * @param bytes     需要转化的byte数组
     * @return          16进制字符串
     */
    private static String toHexString(final byte[] bytes) {
        final Formatter formatter = new Formatter();
        for (final byte b : bytes) {
            //TODO 没有弄明白这里的原因
            //%02x格式控制: 以十六进制输出,2为指定的输出字段的宽度.如果位数小于2,则左端补0
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    /**
     * 对字符串md5加密
     *
     * @param str 要加密的字符串
     * @return  加密后的字符串
     */
    public static String getMD5(String str) throws NoSuchAlgorithmException {
        // 生成一个MD5加密计算摘要
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 计算md5函数
        md.update(str.getBytes());
        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
        return new BigInteger(1, md.digest()).toString(16);
    }
}
