package com.smf.adv;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import java.util.List;

/**
 * RedisTransaction TODO
 *
 * @author hf
 * @date 2024/1/8 13:57:16
 */
@Component
public class RedisTransaction {
    public final static String RS_TRANS_NS = "rts:";

    @Resource
    private JedisPool jedisPool;


    public List<Object> transcation(String... watchKeys) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (watchKeys.length > 0) {
                String watchResult = jedis.watch(watchKeys);
                if (!"OK".equals(watchResult)) {
                    throw new RuntimeException("执行watch失败:" + watchResult);
                }
            }
            Transaction multi = jedis.multi();
            multi.set(RS_TRANS_NS + "test1", "a1");
            multi.set(RS_TRANS_NS + "test2", "a2");
            multi.set(RS_TRANS_NS + "test3", "a3");
            multi.set(RS_TRANS_NS + "test4", "a4");
            multi.set(RS_TRANS_NS + "test5", "a5");
            List<Object> execResult = multi.exec();
            if (execResult == null) {
                throw new RuntimeException("事务无法执行，监视的key被修改:" + watchKeys);
            }
            System.out.println(execResult);
            return execResult;
        } catch (Exception ex) {
            throw new RuntimeException("执行Redis事务失败！", ex);
        } finally {
             if (watchKeys.length>0){
                 jedis.unwatch(); /*前面如果watch了，这里就要unwatch*/
             }
             jedis.close();
        }
    }
}
