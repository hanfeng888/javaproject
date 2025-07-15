package com.smf.lock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * AloneLock TODO
 *
 * @author hf
 * @date 2024/1/8 19:35:03
 */
public class AloneLock implements Lock {

    AtomicReference<Thread> owner = new AtomicReference<>();
    //队列---存储哪些没有抢到锁的线程
    LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<>();

    /**
     * 实现加锁
     */
    @Override
    public void lock() {
        while (!owner.compareAndSet(null, Thread.currentThread())) {
            waiters.add(Thread.currentThread());
            LockSupport.park();//让当前线程阻塞
            waiters.remove(Thread.currentThread());//解锁了，就需要把线程从等待列表中删除
        }
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    /**
     * 实现解锁
     *
     * @throws InterruptedException
     */
    @Override
    public void unlock() {
        if (owner.compareAndSet(Thread.currentThread(), null)) {
            for (Object object : waiters.toArray()) {
                Thread next = (Thread) object;
                LockSupport.unpark(next);
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
