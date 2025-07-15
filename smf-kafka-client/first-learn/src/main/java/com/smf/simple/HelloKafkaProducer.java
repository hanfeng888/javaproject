package com.smf.simple;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * HelloKafkaProducer TODO
 *
 * @author hf
 * @date 2024/1/2 22:26:57
 */
public class HelloKafkaProducer {

    public static void createTopic(){
        Properties props = new Properties();
//        properties.put("bootstrap.servers", "bobo1:9092,bobo2:9092,bobo3:9092");
        props.put("acks", "all"); props.put("retries", 0);
        props.put("batch.size", 16384); props.put("linger.ms", 1);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);

        AdminClient create = KafkaAdminClient.create(props);//创建Topic
        ListTopicsResult listTopicsResult=create.listTopics();
        KafkaFuture<Set<String>> names= listTopicsResult.names();
                create.createTopics(Arrays.asList(new NewTopic("svvt-topic",3,(short)1)));//一个分区
        create.close();//关闭

    }
    public static void main(String[] args) {
//        createTopic();

        //设置属性
        Properties properties = new Properties();

        //指定连接的kafka服务器地址 ,多台服务器用,分割
        properties.put("bootstrap.servers", "bobo1:9092,bobo2:9092,bobo3:9092");
        //设置String的序列化
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        try {
                   
            ProducerRecord<String, String> record;
            try {
                for (int i = 0; i < 100; i++) {
                    record = new ProducerRecord<>("svvt-topic", "teacher"+i++, "feei"+i);
                   Future<RecordMetadata> future= producer.send(record);
                   RecordMetadata recordMetadata=future.get();
                   if (null!=recordMetadata){
                       System.out.println("message is sent.");
                   }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
