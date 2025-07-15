package com.smf.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyRedissonConfig TODO
 *
 * @author hf
 * @date 2024/1/6 14:39:53
 */
@Configuration
public class MyRedissonConfig {

    /**
     * 所有对redisson的使用都是通过RedissonClient
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(){
        Config config=new Config();
        config.useSingleServer().setAddress("redis://192.168.232.121:6379");
        config.setLockWatchdogTimeout(1);//默认情况下，看门狗的检查锁的超时时间是30秒钟
        //根据Config创建出RedissonClient实例
        RedissonClient redisson= Redisson.create(config);
        return redisson;
    }
}
