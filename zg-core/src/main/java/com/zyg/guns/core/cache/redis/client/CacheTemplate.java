package com.zyg.guns.core.cache.redis.client;

import java.util.List;
import java.util.Map;

/**
 * 缓存操作接口
 * Created by wang.yq on 2017/1/4.
 */
public interface CacheTemplate {
    /**
     * 获取缓存值
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return T
     */
    public <T> T get(String key, Class<T> clazz);

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return List<T>
     */
    public <T> List<T> getList(String key, Class<T> clazz);

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getListBatch(String[] keys, Class<T> clazz);


    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getObjectBatch(String[] keys, Class<T> clazz);

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @return
     */
    public List<String> getListBatch(String[] keys);

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Map<String, List<T>> getMapArray(String key, Class<T> clazz);

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Map<String, T> getMap(String key, Class<T> clazz);

    /**
     * 存储缓存值
     *
     * @param key
     * @param value
     * @param timeout
     * @param <T>
     */
    public <T> void set(String key, T value, Integer timeout);

    /**
     * 储存缓存值
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void set(String key, T value);

    /**
     * 精准删除
     *
     * @param key
     */
    public void remove(String key);

    /**
     * 模糊删除(有性能损耗，不推荐使用)
     *
     * @param key
     */
    public void removeLike(String key);

    /**
     * 判断缓存中是否存在
     *
     * @param key
     * @return Boolean
     */
    public Boolean exists(String key);

    /**
     * 全局序列
     *
     * @param key
     * @return Long
     */
    public Long getSequence(String key);

    /**
     * 设置hash字段
     * @param key
     * @param field
     * @param value
     */
    public void hSet(String key, String field, String value);
    /**
     * 获取hash字段
     * @param key
     * @param field
     * @return String
     */
    public String hGet(String key, String field);
    /**
     * 判断hash字段是否存在
     * @param key
     * @param field
     * @return Boolean
     */
    public Boolean hExists(String key, String field);

    /**
     * 删除hash字段
     * @param key
     * @param field
     * @return
     */
    public Long hDel(String key, String field);

   /**
     * 设置setnx
     * @param key
     * @param field
     */
    public Long setnx(String key, String field);

    /**
     * 设置key值过期时间
     * @param key
     * @param timeout
     * @return
     */
    public Long setTimeout(String key, Integer timeout);

    /**
     * 获取key值剩余过期时间
     * @param key
     * @return
     */
    public Long getTtl(String key);
}
