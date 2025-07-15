package com.smf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisConfig TODO
 *
 * @author hf
 * @date 2024/1/6 14:07:47
 */
@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfig {
    @Value("192.168.232.121")
    private String host;

    @Value("6379")
    private int port;
    @Value("300000")
    private int timeout;
    @Value("10")
    private int maxIdle;
    @Value("1500")
    private int maxWaitMillis;
    @Value("false")
    private Boolean blockWhenExhausted;
    @Value("true")
    private Boolean JmxEnabled;

    @Bean
    public JedisPool jedisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        jedisPoolConfig.setJmxEnabled(JmxEnabled);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
        return jedisPool;
    }
}
