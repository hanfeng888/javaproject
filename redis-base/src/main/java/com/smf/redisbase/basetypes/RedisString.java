package com.smf.redisbase.basetypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.print.attribute.standard.MediaSize;
import java.text.MessageFormat;

/**
 * RedisString TODO
 *
 * @author hf
 * @date 2024/1/6 16:42:22
 */
@Component
public class RedisString {
    public final String RS_STR_NS = "rs:";
    @Autowired
    private JedisPool jedisPool;

    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(MessageFormat.format("{0}{1}", RS_STR_NS, key), value);
        } catch (Exception ex) {
            throw new RuntimeException("向Redis中存值失败!");
        } finally {
            jedis.close();
        }
    }

    /**
     * 批量向Redis中存值，永久有效
     *
     * @param keysvalues
     * @return
     */
    public String msetRaw(String... keysvalues) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            /*自己去拆解，加前缀，再组合...*/
            return jedis.mset(keysvalues);
        } catch (Exception ex) {
            throw new RuntimeException("批量向Redis中存值失败!");
        } finally {
            jedis.close();
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(RS_STR_NS + key);
        } catch (Exception ex) {
            throw new RuntimeException("获取Redis值失败!");
        } finally {
            jedis.close();
        }
    }
}
