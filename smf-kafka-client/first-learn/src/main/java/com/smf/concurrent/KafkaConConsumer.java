package com.smf.concurrent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * KafkaConConsumer TODO
 *
 * @author hf
 * @date 2024/1/4 11:00:49
 */
public class KafkaConConsumer {
    public static final int CONCURRENT_PARTITIONS_COUNT = 2;
    private static ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_PARTITIONS_COUNT);
   

    private static class ConsumerWorker implements Runnable {

        private KafkaConsumer<String, String> consumer;

        public ConsumerWorker(Map<String, Object> config, String topic) {
            Properties properties = new Properties();
            properties.putAll(config);
            this.consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Collections.singletonList(topic));
        }

        @Override
        public void run() {
            final String ThreadName = Thread.currentThread().getName();
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println(MessageFormat.format("{0}|{1}", ThreadName, MessageFormat.format(
                                "主题:{0},分区: {1},偏移量 {2},key:{3},value:{4}", record.topic(), record.partition(),
                                record.offset(), record.key(), record.value()
                        )));
                    }
                }
            } finally {
                consumer.close();
            }
        }
    }

    public static void main(String[] args) {
        Map<String,Object> properties=new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"bobo1:9092,bobo2:9092,bobo3:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"c_test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        for (int i = 0; i < CONCURRENT_PARTITIONS_COUNT; i++) {
              executorService.submit(new ConsumerWorker(properties,"concurrent-Consumer"));

        }
    }
}
