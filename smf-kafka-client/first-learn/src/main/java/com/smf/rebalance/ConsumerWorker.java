package com.smf.rebalance;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.internals.ConsumerMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ConsumerWorker TODO
 *
 * @author hf
 * @date 2024/1/4 14:19:56
 */
public class ConsumerWorker implements Runnable {

    private final KafkaConsumer<String, String> consumer;
    //用来保存每个消费者当前读取分区的偏移量
    private final Map<TopicPartition, OffsetAndMetadata> currOffsets;
    private final boolean isStop;

    public ConsumerWorker(boolean isStop) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "bobo1:9092,bobo2:9092,bobo3:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, RebalanceConsumer.GROUP_ID);
        //取消自动提交
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        this.isStop = isStop;
        this.consumer = new KafkaConsumer<String, String>(properties);
        this.currOffsets = new HashMap<>();
        consumer.subscribe(Collections.singletonList("rebalance"), new HandleRebalance(currOffsets, consumer));
    }

    @Override
    public void run() {
        final String id = Thread.currentThread().getId() + "";
        int count = 0;
        TopicPartition topicPartition = null;
        long offset = 0;
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(MessageFormat.format(
                            "{0}|处理主题：{1},分区:{2},偏移量:{3},key:{4},value:{5}",
                            id, record.topic(), record.partition(), record.offset(),
                            record.key(), record.value()
                    ));
                    topicPartition = new TopicPartition(record.topic(), record.partition());
                    offset = record.offset() + 1;
                    //获取偏移量
                    currOffsets.put(topicPartition, new OffsetAndMetadata(offset, "no"));
                    count++;
                    //执行业务sql
                    //execute business sql
                }
                if (currOffsets.size() > 0) {
                    for (TopicPartition partitionKey : currOffsets.keySet()) {
                        HandleRebalance.partitionOffsetMap.put(partitionKey, currOffsets.get(partitionKey).offset());
                    }
                    //提交事务，同时将业务和偏移量入库（使用HashMap替代）
                    //the transcation is committed while the business and offset are stored ( use HashMap instead)
                }
                if (isStop && count >= 20) {   //监听线程
                    System.out.println(id + "将关闭，当前偏移量为:" + currOffsets);
                    System.out.println(id + "will close，the current offset is:" + currOffsets);
                    consumer.commitSync();
                    break;
                }
                consumer.commitSync();
                
                //TODO 优雅退出
                // to exit gracefully
                consumer.wakeup();

            }
        } catch (Exception ex) {
            consumer.close();
        }
    }
}
