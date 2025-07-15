package com.smf.redisbase.basetypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * RedisHash TODO
 * 操作hash类型
 *
 * @author hf
 * @date 2024/1/6 16:52:01
 */
@Component
public class RedisHash {
    public final static String RS_HASH_NS = "rh:";
    @Autowired
    private JedisPool jedisPool;

    /**
     * 向Redis中存储，永久有效
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long set(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(RS_HASH_NS + key, field, value);
        } catch (Exception ex) {
            throw new RuntimeException("向Redis中存值失败!");
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据传入key获取指定value
     * @param key
     * @param field
     * @return
     */
    public String get(String key, String field) {
        return getRaw(RS_HASH_NS + key, field);
    }

    /**
     * 根据传入Key获取指定Value
     *
     * @param key
     * @param field
     * @return
     */
    public String getRaw(String key, String field) {
        Jedis jedis = null;
        String value;
        try {
            jedis = jedisPool.getResource();
            value = jedis.hget(key, field);
        } catch (Exception ex) {
            throw new RuntimeException("获取Redis值失败！");
        } finally {
            jedis.close();
        }
        return value;
    }


}
