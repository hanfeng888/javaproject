package com.smf.lock;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * RedisDiskLock TODO
 *
 * @author hf
 * @date 2024/1/8 19:41:10
 */
@Component
public class RedisDiskLock implements Lock {

    private static final int LOCK_TIME = 5 * 1000;
    private static final String RS_DISTLOCK_NS = "tdln:";

    private static final String RELEASE_LOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then\n" +
            "    return redis.call('del', KEYS[1])\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    //保存每个线程的独有的ID值
    private ThreadLocal<String> lockerId = new ThreadLocal<>();

    //解决锁的重入
    private Thread ownerThread;
    private String lockName = "lock";

    @Resource
    private JedisPool jedisPool;

    @Override
    public void lock() {
        while (!tryLock()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("不支持可中断获取锁!");
    }

    @Override
    public boolean tryLock() {
        Thread t = Thread.currentThread();
        if (ownerThread == t) {
            //说明本线程持有锁
            return false;
        } else if (ownerThread != null) {
            //本进程里有其他线程持有分布式锁
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String id = UUID.randomUUID().toString();
            SetParams params = new SetParams();
            params.px(LOCK_TIME);
            params.nx();
            synchronized (this) {
                if ((ownerThread == null) && "OK".equals(jedis.set(RS_DISTLOCK_NS + lockName, id, params))) {
                    lockerId.set(id);
                    setOwnerThread(t);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("分布式锁尝试加锁失败!");
        } finally {
            jedis.close();
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("不支持等待尝试获取锁!");
    }

    @Override
    public void unlock() {
        if (ownerThread != Thread.currentThread()) {
            throw new RuntimeException("试图释放无所有权的锁!");
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = (Long) jedis.eval(RELEASE_LOCK_LUA,
                    Arrays.asList(RS_DISTLOCK_NS + lockName),
                    Arrays.asList(lockerId.get()));
            if (result.longValue() != 0L) {
                System.out.println("Redis上的锁已经释放!");
            } else {
                System.out.println("Redis上的锁释放失败!");
            }
        } catch (Exception e) {
            throw new RuntimeException("释放锁失败!", e);
        } finally {
            if (jedis != null) jedis.close();
            lockerId.remove();
            setOwnerThread(null);
            System.out.println("本地锁所有权已释放！");
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("不支持等待通知操作！");
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public Thread getOwnerThread() {
        return ownerThread;
    }

    public void setOwnerThread(Thread ownerThread) {
        this.ownerThread = ownerThread;
    }
}
