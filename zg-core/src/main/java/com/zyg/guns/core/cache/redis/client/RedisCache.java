package com.zyg.guns.core.cache.redis.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyg.guns.core.utils.DateUtil;
import com.zyg.guns.core.utils.JSONUtil;
import com.zyg.guns.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.*;

/**
 * Redis缓存实现
 * Created by wang.yq on 2017/1/4.
 */
@Component
public class RedisCache implements CacheTemplate {
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

    @Resource
    private JedisPool jedisPool;

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        try {
            return JSONUtil.parseObject(jedis.get(key), clazz);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        try {
            return JSONUtil.parseArray(jedis.get(key), clazz);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @param clazz
     * @return
     */
    @Override
    public <T> List<T> getListBatch(String[] keys, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        try {
            List<T> result = null;
            List<String> jsonArray = jedis.mget(keys);
            if (jsonArray != null && jsonArray.size() > 0) {
                result = new ArrayList<>();
                for (String str : jsonArray) {
                    List<T> array = JSONUtil.parseArray(str, clazz);
                    if (array != null) {
                        result.addAll(array);
                    }
                }
                return result;
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @param clazz
     * @return
     */
    @Override
    public <T> List<T> getObjectBatch(String[] keys, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        try {
            List<T> result = null;
            List<String> jsonArray = jedis.mget(keys);
            if (jsonArray != null && jsonArray.size() > 0) {
                result = new ArrayList<>();
                for (String str : jsonArray) {
                    result.add(JSONUtil.parseObject(str, clazz));
                }
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 批量获取缓存值列表
     *
     * @param keys
     * @return
     */
    @Override
    public List<String> getListBatch(String[] keys) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.mget(keys);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> Map<String, List<T>> getMapArray(String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        try {
            Map<String, JSONArray> maps = this.get(key, Map.class);
            Map<String, List<T>> conventResult = null;
            if (maps != null && maps.size() > 0) {
                conventResult = new HashMap<>();
                Iterator<String> iterator = maps.keySet().iterator();
                String mapKey;
                while (iterator.hasNext()) {
                    mapKey = iterator.next();
                    List<T> arrays = JSONUtil.parseArray(maps.get(mapKey).toJSONString(), clazz);
                    conventResult.put(mapKey, arrays);
                }
            }
            return conventResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    /**
     * 获取缓存值列表
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> Map<String, T> getMap(String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        try {
            Map<String, JSONObject> maps = this.get(key, Map.class);
            Map<String, T> conventResult = null;
            if (maps != null && maps.size() > 0) {
                conventResult = new HashMap<>();
                Iterator<String> iterator = maps.keySet().iterator();
                String mapKey;
                while (iterator.hasNext()) {
                    mapKey = iterator.next();
                    conventResult.put(mapKey, JSONUtil.parseObject(maps.get(mapKey).toJSONString(), clazz));
                }
            }
            return conventResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public <T> void set(String key, T value, Integer timeout) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.setex(key, timeout, JSONUtil.toString(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存缓存值
     *
     * @param key
     * @param value
     */
    @Override
    public <T> void set(String key, T value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, JSONUtil.toString(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void remove(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void removeLike(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            Set<String> keys = jedis.keys(key + "*");
            if (!CollectionUtils.isEmpty(keys)) {
                jedis.del(keys.toArray(new String[keys.size()]));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    @Override
    public Long getSequence(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            int timeout = 3600;//超时时间为1小时
            Long seq = jedis.incr(key);
            jedis.expire(key, timeout);
            //14位日期+4位数字
            StringBuilder builder = new StringBuilder();
            builder.append(DateUtil.getNow(DateUtil.PATTERN_SIMPLE_DATETIME));
            builder.append(StringUtil.leftFill(String.valueOf(seq), '0', 4));
            return Long.valueOf(builder.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public void hSet(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public String hGet(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public Boolean hExists(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Long hDel(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hdel(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

     @Override
    public Long setnx(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.setnx(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        logger.error("设置"+key+"setnx发生异常返回0");
        return 0L;
    }

    @Override
    public Long setTimeout(String key, Integer timeout) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.expire(key, timeout);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        logger.error("设置"+key+"过期时间发生异常返回0");
        return 0L;
    }

    @Override
    public Long getTtl(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.ttl(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        logger.error("获取"+key+"ttl时间发生异常返回0");
        return 0L;
    }
}
