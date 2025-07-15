package com.smf;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * StreamCopy TODO
 *
 * @author hf
 * @date 2024/1/7 16:09:04
 */
public class StreamCopy {
    public static void main(String[] args) {
        Properties properties = new Properties();

        //指定连接的kafka服务器地址 ,多台服务器用,分割
        properties.put("bootstrap.servers", "bobo1:9092,bobo2:9092,bobo3:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "copy");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass()); //输入key的类型
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());             //输入value的类型

        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("sell").to("sell-2");
        final Topology topo = builder.build();
        final KafkaStreams streams = new KafkaStreams(topo, properties);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("strem") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            //这里才开始进行流计算
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);

    }
}
