package com.smf.simple;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * HelloKafkaConsumer TODO
 *
 * @author hf
 * @date 2024/1/2 23:14:39
 */
public class HelloKafkaConsumer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        //指定连接的kafka服务器地址 ,多台服务器用,分割
        properties.put("bootstrap.servers", "bobo1:9092,bobo2:9092,bobo3:9092");
        //设置String的序列化
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);

        properties.put("auto.offset.reset", "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        //构建kafka消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        try {
            consumer.subscribe(Collections.singletonList("svvt-topic"));
//            consumer.assign(Arrays.asList(new TopicPartition("smf-topic")));
            //调用消费者拉取消息
            while (true) {
                //每隔1秒拉取一次消息
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String value = record.value();
                    System.out.println("接收到消息:key=" + key + ",value=" + value);
                }
            }
        } finally {
            //释放连接
            consumer.close();
        }
    }
}
