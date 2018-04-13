package com.zyg.guns.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * JSON转换工具类[使用FastJSON实现]
 * Created by wyq on 2016/6/13.
 */
public class JSONUtil {

    /**
     * 将对象转换为Json字符串
     *
     * @param t 对象
     * @return String 转换后的Json字符串
     */
    public static <T> String toString(T t) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        return toString(t, dateFormat);
    }

    /**
     * 将对象转换为Json字符串
     *
     * @param t          对象
     * @param dateFormat 日期类型转换格式
     * @param <T>
     * @return String 转换后的Json字符串
     */
    public static <T> String toString(T t, String dateFormat) {
        JSON.DEFFAULT_DATE_FORMAT = dateFormat;
        return JSON.toJSONString(t, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 解析Json字符串为对象
     *
     * @param json  Json字符串
     * @param clazz 目标对象类型
     * @return T 目标对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 解析Json字符串为对象列表
     *
     * @param json  Json字符串
     * @param clazz 目标对象类型
     * @return List<T> 目标对象列表
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }
}
