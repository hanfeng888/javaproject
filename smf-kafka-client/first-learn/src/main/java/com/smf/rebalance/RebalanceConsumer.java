package com.smf.rebalance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RebalanceConsumer TODO
 *
 * @author hf
 * @date 2024/1/4 14:18:28
 */
public class RebalanceConsumer {
    public static final String GROUP_ID = "rebalance_consumer";
    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            executorService.submit(new ConsumerWorker(false));
        }
        for (int i = 0; i < 5; i++) {
            Thread.sleep(10000);
            new Thread(new ConsumerWorker(true)).start();
        }
       

    }
}
