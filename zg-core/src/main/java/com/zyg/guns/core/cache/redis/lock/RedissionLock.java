package com.zyg.guns.core.cache.redis.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissionLock {

    @Autowired
    Redisson redisson;

    private static final String LOCK_PRE = "redissonLock_";

    /**
     *  获取指定redisson rlock
     * @param key
     * @return
     */
    public RLock getLock(String key){
       return redisson.getLock(LOCK_PRE+key);
    }

    /**
     *  获取redission
     * @return
     */
    public Redisson getRedission(){
        return redisson;
    }

    /**
     * redisson锁，默认锁定30秒，30秒后自动解锁
     * @param key key值
     */
    public boolean lock(String key){
        RLock rLock = getLock(key);
        rLock.lock(30, TimeUnit.SECONDS);
        //lock提供带timeout参数，timeout结束强制解锁，防止死锁
        return  true;
    }

    /**
     * 带超时时间锁定
     * @param key key值
     * @param timeout  锁定毫秒数
     * @return
     */
    public boolean lock(String key,Integer timeout){
        RLock rLock = getLock(key);
        rLock.lock(timeout, TimeUnit.MILLISECONDS); //lock提供带timeout参数，timeout结束强制解锁，防止死锁
        return  true;
    }

    public void unlock(String key){
        RLock rLock = getLock(key);
        rLock.unlock();
    }
}
