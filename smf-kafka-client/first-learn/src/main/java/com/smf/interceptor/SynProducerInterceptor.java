package com.smf.interceptor;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.Properties;

/**
 * SynProducerInterceptor TODO
 *
 * @author hf
 * @date 2024/1/5 11:31:48
 */
public class SynProducerInterceptor {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "bobo1:9092,bobo2:9092,bobo3:9092");
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);
        ArrayList<String> interceptors = new ArrayList<>();
        interceptors.add("com.smf.interceptor.SelfInterceptor");
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        try {

        } catch (Exception ex) {

        }
    }
}
