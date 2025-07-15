package com.smf.limit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * IsAcquire TODO
 *
 * @author hf
 * @date 2024/1/8 14:43:40
 */
@Service
public class IsAcquire {
    private DefaultRedisScript<Long> getRedisScript;

    public boolean acquire(String limitKey, int limit, int expire) throws Exception {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(Long.class);//执行脚本返回值 Long
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("rateLimiter.lua")));
        Long result = (Long) jedis.eval(getRedisScript.getScriptAsString(), 1, limitKey,
                String.valueOf(limit),
                String.valueOf(expire)
        );
        if (result == 0) {
            return false;
        }
        return true;
    }

}
