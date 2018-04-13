package com.zyg.guns.core.cache.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConfigurationProperties(prefix="redis")
@EnableCaching
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
public class RedisConfig {
	private String host;
	private Integer port;
	private Integer maxTotal;
	private Integer maxIdl;
	private Integer minIdl;
	private Integer maxWaitMills;
	private Boolean testOneReturn;
	private Boolean testOneBorrow;
	private Boolean testWhileIdl;
	private Integer timeBetweenEvictionRunsMillis;
	private Integer numTestsPerEvictionRun;
	private Integer MinEvictableIdleTimeMillis;
	private Integer database = 0;
	
	@Bean(name = "jedisPoolConfig")
    public JedisPoolConfig redisConnectionFactory() {
        // 链接池配置
        JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdl);
        config.setMinIdle(minIdl);//设置最小空闲数
        config.setMaxWaitMillis(maxWaitMills);
        config.setTestOnBorrow(testOneReturn);
        config.setTestOnReturn(testOneBorrow);
        //Idle时进行连接扫描
        config.setTestWhileIdle(testWhileIdl);
        //表示idle object evitor两次扫描之间要sleep的毫秒数
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //表示idle object evitor每次扫描的最多的对象数
        config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        config.setMinEvictableIdleTimeMillis(MinEvictableIdleTimeMillis);
       
        return config;
    }
	@Bean( name = "jedisPool")
	public JedisPool getJedisPool(JedisPoolConfig jedisPoolConfig) {
		JedisPool pool = new JedisPool(jedisPoolConfig, host, port, 2000, null, database);
		return pool;
	}
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}
	public Integer getMaxIdl() {
		return maxIdl;
	}
	public void setMaxIdl(Integer maxIdl) {
		this.maxIdl = maxIdl;
	}
	public Integer getMinIdl() {
		return minIdl;
	}
	public void setMinIdl(Integer minIdl) {
		this.minIdl = minIdl;
	}
	public Integer getMaxWaitMills() {
		return maxWaitMills;
	}
	public void setMaxWaitMills(Integer maxWaitMills) {
		this.maxWaitMills = maxWaitMills;
	}
	public Boolean getTestOneReturn() {
		return testOneReturn;
	}
	public void setTestOneReturn(Boolean testOneReturn) {
		this.testOneReturn = testOneReturn;
	}
	public Boolean getTestOneBorrow() {
		return testOneBorrow;
	}
	public void setTestOneBorrow(Boolean testOneBorrow) {
		this.testOneBorrow = testOneBorrow;
	}
	public Boolean getTestWhileIdl() {
		return testWhileIdl;
	}
	public void setTestWhileIdl(Boolean testWhileIdl) {
		this.testWhileIdl = testWhileIdl;
	}
	public Integer getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}
	public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}
	public Integer getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}
	public void setNumTestsPerEvictionRun(Integer numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}
	public Integer getMinEvictableIdleTimeMillis() {
		return MinEvictableIdleTimeMillis;
	}
	public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
		MinEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }
}
