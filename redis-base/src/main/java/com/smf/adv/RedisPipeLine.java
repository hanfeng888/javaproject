package com.smf.adv;

import jdk.nashorn.internal.scripts.JD;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * RedisPipeLine TODO
 *
 * @author hf
 * @date 2024/1/8 12:11:54
 */
@Component
public class RedisPipeLine {
    @Resource
    private JedisPool jedisPool;

    public List<Object> plGet(List<String> keys) {
        
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            pipeline.multi();//开启事务
            //。。。。。
            pipeline.exec(); //提交事务
            for (String key : keys) {
                pipeline.get(key);
            }
            return pipeline.syncAndReturnAll(); //这里只会向redis发送一次请求

        } catch (Exception ex) {
            throw new RuntimeException("执行pipeline获取失败!", ex);
        } finally {
            jedis.close();
        }
    }

    public void plSet(List<String> keys, List<String> values) {
        if (keys.size() != values.size()) {
            throw new RuntimeException("key和value个数不匹配！");
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipelined = jedis.pipelined();
            for (int i = 0; i < keys.size(); i++) {
                pipelined.set(keys.get(i), values.get(i));
            }
            pipelined.sync();
        } catch (Exception e) {
            throw new RuntimeException("执行Pipeline设值失败！", e);
        } finally {
            jedis.close();
        }
    }

    public void plSetMap(Map<String, String> kvMap) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipelined = jedis.pipelined();
            kvMap.forEach(pipelined::set);
            pipelined.sync();
        } catch (Exception e) {
            throw new RuntimeException("执行Pipeline设值失败！", e);
        } finally {
            jedis.close();
        }
    }
}
