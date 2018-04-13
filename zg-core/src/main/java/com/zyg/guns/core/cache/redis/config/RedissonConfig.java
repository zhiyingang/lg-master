package com.zyg.guns.core.cache.redis.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@ConfigurationProperties(prefix="redisson")
public class RedissonConfig implements Serializable{
    private static Config config = new Config();
    private static Redisson redisson = null;

    //redis端口
    private String address;
    private String model;

    @Bean( name = "redisson")
    public Redisson getRedisson() {
        return getInstance();
    }

    /**
     * 创建redission锁
     */
    public void creatRedisson(){
        try {
            if(model.equals("single")){
                config.useSingleServer().setAddress(address);
            }else{
                config.useClusterServers() //这是用的集群server
                        .setScanInterval(2000) //设置集群状态扫描时间
                        .setMasterConnectionPoolSize(10000) //设置连接数
                        .setSlaveConnectionPoolSize(10000)
                        .addNodeAddress(address);
            }
            redisson = (Redisson) Redisson.create(config);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 单例获取redisson实例
     * @return
     */
    public Redisson getInstance() {
        if (redisson == null) {
            synchronized (RedissonConfig.class){
                if(redisson == null){
                    creatRedisson();
                }
            }
        }
        return redisson;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
