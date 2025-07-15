package com.smf.redisbase.redismq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;


/**
 * 基于PUBSUB的消息中间件的实现
 *
 * @author hf
 * @date 2024/1/6 20:39:31
 */
@Component
public class PSVer extends JedisPubSub {
    public final static String RS_PS_MQ_HS = "rpsm:";
    @Autowired
    private JedisPool jedisPool;

    @Override
    public void onMessage(String channel, String message) {
        System.out.println("Accept" + channel + "message:" + message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("Subscribe" + channel + "count:" + subscribedChannels);
    }

    public void pub(String channel,String message){
        
    }
}
