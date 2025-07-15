package com.smf.redisbase.basetypes;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * RedisGlobl TODO
 *
 * @author hf
 * @date 2024/1/6 17:36:12
 */
public class RedisGlobl {
    @Autowired
    private JedisPool jedisPool;

    public Set<String> keys(String pattern) {
        Jedis jedis = null;
        Set<String> keys;
        try {
            jedis = jedisPool.getResource();
            keys = jedis.keys(pattern);

        } catch (Exception ex) {
            return null;
        } finally {
            jedis.close();
        }
        return keys;
    }

    public Long dbSize() {
        Jedis jedis = null;
        Long dbSize;
        try {
            jedis = jedisPool.getResource();
            dbSize = jedis.dbSize();
        } catch (Exception ex) {
            return null;
        } finally {
            jedis.close();
        }
        return dbSize;
    }

    public Boolean existsKey(String key) {
        Jedis jedis = null;
        Boolean isExists;
        try {
            jedis = jedisPool.getResource();
            isExists = jedis.exists(key);
        } catch (Exception ex) {
            return false;
        } finally {
            jedis.close();
        }
        return isExists;
    }

    public Long expire(String key, long seconds) {
        Jedis jedis = null;
        Long result;
        try {
            jedis = jedisPool.getResource();
            result = jedis.expire(key, seconds);
        } catch (Exception ex) {
            return null;
        } finally {
            jedis.close();
        }
        return result;
    }

    public Long ttl(String key) {
        Jedis jedis = null;
        Long duration;
        try {
            jedis = jedisPool.getResource();
            duration = jedis.ttl(key);
        } catch (Exception ex) {
            return null;
        } finally {
            jedis.close();
        }
        return duration;
    }

}
