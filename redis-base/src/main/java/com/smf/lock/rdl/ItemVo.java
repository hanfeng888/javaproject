package com.smf.lock.rdl;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * ItemVo TODO
 *
 * @author hf
 * @date 2024/1/9 10:59:15
 */
public class ItemVo<T> implements Delayed {
    private long activeTime;
    private T data;

    public ItemVo(long expirationTime, T data) {
        super();
        this.activeTime = expirationTime+System.currentTimeMillis()-100;
        this.data = data;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long d = unit.convert(this.activeTime
                - System.currentTimeMillis(),unit);
        return d;
    }

    @Override
    public int compareTo(Delayed o) {
        long d = (getDelay(TimeUnit.MILLISECONDS)
                -o.getDelay(TimeUnit.MILLISECONDS));
        if (d==0){
            return 0;
        }else{
            if (d<0){
                return -1;
            }else{
                return  1;
            }
        }
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
