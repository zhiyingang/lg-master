package com.zyg.guns.core.cache.redis.lock;

import com.zyg.guns.core.cache.redis.client.CacheKeyUtil;
import com.zyg.guns.core.cache.redis.client.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Component
public class RedisLock implements Serializable{
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

    @Resource
    private JedisPool jedisPool;

    /**
     * 加锁默认请求锁时间5s
     * @param key  锁的key
     * @Param 获取锁时间
     * @return 锁标识
     */
    public String addSetnx(String key) {
        int endTime = 1000*30;
        int timeout = 1000*30;
        return setnxWithTime(key,endTime,timeout);
    }

    /**
     * 加锁
     * @param key  锁的key
     * @param acquireTimeout  获取超时时间
     * @param timeout   锁的超时时间
     * @return 锁标识
     */
    public  String setnxWithTime(String key,
                       long acquireTimeout, long timeout) {
        Jedis conn = null;
        String retIdentifier = null;
        try {
            // 获取连接
            conn = jedisPool.getResource();
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = CacheKeyUtil.buildLockKey(key);
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int)(timeout);
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                logger.info("等待设置锁"+lockKey);
                if (conn.setnx(lockKey, identifier)==1) {
                    conn.expire(lockKey, lockExpire);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    logger.info("获取到锁"+lockKey);
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                if (conn.ttl(lockKey) == -1) {
                    conn.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     * @param key 锁的key
     * @param identifier    释放锁的标识
     * @return
     */
    public boolean delSetnx(String key, String identifier) {
        Jedis conn = null;
        // 锁名，即key值
        String lockKey = CacheKeyUtil.buildLockKey(key);
        boolean retFlag = false;
        try {
            conn = jedisPool.getResource();
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }
}
