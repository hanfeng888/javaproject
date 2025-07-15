package com.smf.redisbase.advtypes.bitmap;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * RedissonBF TODO
 *
 * @author hf
 * @date 2024/1/6 16:03:20
 */
public class RedissonBF {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.232.121:6379");
        config.setLockWatchdogTimeout(1);//默认情况下，看门狗的检查锁的超时时间是30秒钟
        //根据Config创建出RedissonClient实例
        RedissonClient redisson = Redisson.create(config);

        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("phoneList");
      
        bloomFilter.tryInit(100L<<20, 0.03);
        bloomFilter.add("10081");
        bloomFilter.add("10082");
        bloomFilter.add("10083");
        bloomFilter.add("10084");
        bloomFilter.add("10085");
        bloomFilter.add("10086");
        System.out.println("123456:BF--" + bloomFilter.contains("123456"));
        System.out.println("10086:BF--" + bloomFilter.contains("10086"));
        System.out.println("10084:BF--" + bloomFilter.contains("10084"));

    }
}
