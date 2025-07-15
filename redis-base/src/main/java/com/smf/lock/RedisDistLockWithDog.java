package com.smf.lock;

import com.smf.lock.rdl.ItemVo;
import com.smf.lock.rdl.LockItem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * RedisDistLockWithDog TODO
 *
 * @author hf
 * @date 2024/1/9 10:51:55
 */
public class RedisDistLockWithDog implements Lock {
    private final static int LOCK_TIME = 1 * 1000;
    private final static String LOCK_TIME_STR = String.valueOf(LOCK_TIME);
    private final static String RS_DISTLOCK_NS = "tdln2:";
    /*
     if redis.call('get',KEYS[1])==ARGV[1] then
        return redis.call('del', KEYS[1])
    else return 0 end
     */
    private final static String RELEASE_LOCK_LUA =
            "if redis.call('get',KEYS[1])==ARGV[1] then\n" +
                    "        return redis.call('del', KEYS[1])\n" +
                    "    else return 0 end";
    private ThreadLocal<String> lockerId = new ThreadLocal<>();
    private Thread ownerThread;
    private String lockName = "lock";

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

    private Thread expireThread;
    private static DelayQueue<ItemVo<LockItem>> delayDog = new DelayQueue<>();
    //续锁逻辑：判断是持有锁的线程才能续锁
    private final static String DELAY_LOCK_LUA =
            "if redis.call('get',KEYS[1])==ARGV[1] then\n" +
                    "        return redis.call('pexpire', KEYS[1],ARGV[2])\n" +
                    "    else return 0 end";
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
        throw new UnsupportedOperationException("不支持可中断获取锁！");
    }

    @Override
    public boolean tryLock() {
        Thread t = Thread.currentThread();
        /*说明本线程正在持有锁*/
        if (ownerThread == t) {
            return true;
        } else if (ownerThread != null) {/*说明本进程中有别的线程正在持有分布式锁*/
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            /*每一个锁的持有人都分配一个唯一的id，也可采用snowflake算法*/
            String id = UUID.randomUUID().toString();

            SetParams params = new SetParams();
            params.px(LOCK_TIME); //加锁时间1s
            params.nx();
            synchronized (this) {
                if ((ownerThread == null) &&
                        "OK".equals(jedis.set(RS_DISTLOCK_NS + lockName, id, params))) {
                    lockerId.set(id);
                    setOwnerThread(t);
                    if (expireThread == null) {//看门狗线程启动
                        expireThread = new Thread(new ExpireTask(), "expireThread");
                        expireThread.setDaemon(true);
                        expireThread.start();
                    }
                    //往延迟阻塞队列中加入元素（让看门口可以在过期之前一点点的时间去做锁的续期）
                    delayDog.add(new ItemVo<>((int) LOCK_TIME, new LockItem(lockName, id)));
                    System.out.println(Thread.currentThread().getName() + "已获得锁----");
                    return true;
                } else {
                    System.out.println(Thread.currentThread().getName() + "无法获得锁----");
                    return false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("分布式锁尝试加锁失败！", e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
