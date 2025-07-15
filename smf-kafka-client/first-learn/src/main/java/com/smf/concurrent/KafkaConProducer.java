package com.smf.concurrent;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.*;

/**
 * KafkaConProducer TODO
 *
 * @author hf
 * @date 2024/1/4 11:01:01
 */
public class KafkaConProducer {
    private static final int MSG_SIZE = 1000;
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static CountDownLatch countDownLatch = new CountDownLatch(MSG_SIZE);

    private static User makeUser(int id) {
        User user = new User(id);
        String userName = "msb_" + id;
        user.setName(userName);
        return user;
    }

    private static class ProduceWorker implements Runnable {

        private ProducerRecord<String, String> record;
        private KafkaProducer<String, String> producer;

        public ProduceWorker(ProducerRecord<String, String> record, KafkaProducer<String, String> producer) {
            this.record = record;
            this.producer = producer;
        }

        @Override
        public void run() {
            final String ThreadName = Thread.currentThread().getName();
            try {
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (null != exception) {
                            exception.printStackTrace();
                        }
                        if (null != metadata) {
                            System.out.println(ThreadName + "|" + String.format("偏移量:%s,分区:%s", metadata.offset(), metadata.partition()));
                        }
                    }
                });
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        //指定连接的kafka服务器地址 ,多台服务器用,分割
        properties.put("bootstrap.servers", "bobo1:9092,bobo2:9092,bobo3:9092");
        //设置String的序列化
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        try {
            for (int i = 0; i < MSG_SIZE; i++) {
                User user = makeUser(i);
                ProducerRecord<String, String> record = new ProducerRecord<>("concurrent-Consumer", null,
                        System.currentTimeMillis(), user.getId() + "", user.toString());
                executorService.submit(new ProduceWorker(record, producer));
            }
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
            executorService.shutdown();
        }
    }
}
