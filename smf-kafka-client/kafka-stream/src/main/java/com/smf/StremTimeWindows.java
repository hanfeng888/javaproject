package com.smf;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import com.sun.org.apache.xml.internal.serializer.ToStream;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * 类说明：使用stream做统计：且加过滤，且使用时间窗口
 *
 * @author hf
 * @date 2024/1/7 16:29:30
 */
public class StremTimeWindows {
    public static void main(String[] args) {
        //设置属性
        Properties properties = new Properties();
        //每个stream应用都必须有唯一的id
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-window-555");
        //指定连接的kafka服务器的地址
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 3000);  //提供时间设置为3秒
        //输入key的类型
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //输入value的类型
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //创建流构造器
        StreamsBuilder builder = new StreamsBuilder();
        KTable<Windowed<String>, Long> count = builder.stream("sell")
                .flatMapValues((value) -> {
                    String[] split = value.toString().split(" ");
                    List<String> strings = Arrays.asList(split);
                    return strings;
                })
                .map((k, v) -> new KeyValue<>(v, v)).filter((key, value) -> (!value.equals("iphone")))
                .groupByKey().
                windowedBy(TimeWindows.of(Duration.ofSeconds(5)).advanceBy(Duration.ofSeconds(3)))
                .count();
        count.toStream().foreach((k, v) -> System.out.println("key:" + k + " " + "value:" + v));
        count.toStream().map((x, y) -> {
            return new KeyValue<>(x.toString(), y.toString());
        }).to("wordcount-output-window");
        final Topology topo = builder.build();
        final KafkaStreams streams = new KafkaStreams(topo, properties);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("stream") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
