//package com.smf;
//
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.KafkaListeners;
//import org.springframework.messaging.handler.annotation.SendTo;
//
//
///**
// * KafkaSpringApplication TODO
// *
// * @author hf
// * @date 2024/1/3 20:02:27
// */
//@SpringBootApplication
////@EnableKafka
////@EnableKafkaStreams
//public class KafkaSpringApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(KafkaSpringApplication.class, args);
//    }
//
//
//    @KafkaListeners(value = {@KafkaListener(topics = {"hellpo"})})
//    public void recevice(ConsumerRecord<String, String> record) {
//        System.out.println("record:" + record);
//    }
//
//    @KafkaListeners(value = {@KafkaListener(topics = {"hellpo01"})})
//    @SendTo("hellpo02")
//    public String recevice01(ConsumerRecord<String, String> record) {
//        System.out.println("record:" + record);
//        return record.value()+"\t"+"mashibing edu" ;
//    }
//}
