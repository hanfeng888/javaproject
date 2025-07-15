package com.smf.rebalance;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HandleRebalance TODO
 *
 * @author hf
 * @date 2024/1/4 14:28:26
 */
public class HandleRebalance implements ConsumerRebalanceListener {

    public static final Map<TopicPartition, Long> partitionOffsetMap = new ConcurrentHashMap<>();
    private final KafkaConsumer<String, String> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currOffsets;

    public HandleRebalance(Map<TopicPartition, OffsetAndMetadata> currOffsets,
                           KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
        this.currOffsets = currOffsets;
    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        // save the offsets in an external store using some custom code not described here
//        for(TopicPartition partition: partitions)
//            saveOffsetInExternalStore(consumer.position(partition));
        final String id = Thread.currentThread().getId() + "";
        System.out.println(MessageFormat.format("{0}--onPartitionRevoked参数值为:{1}", id, partitions));
        System.out.println(MessageFormat.format("{0}-服务器准备分区在均衡，提交偏移量。当前偏移量为:{1}", id, currOffsets));
        /*
         * 我们可以不使用 consumer.commitSync(curroffsets)
         * 提交偏移量到kafka，由我们自己维护
         * 开始事务
         * 偏移量写入数据库
         * */

//        System.out.println(MessageFormat.format("分区偏移量表中：{}", partitionOffsetMap.toString()));
        for (TopicPartition partition : partitions) {
            partitionOffsetMap.put(partition, currOffsets.get(partition).offset());
        }
        consumer.commitSync(currOffsets);
    }

    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        // do not need to save the offsets since these partitions are probably owned by other consumers already
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        // read the offsets from an external store using some custom code not described here
//        for(TopicPartition partition: partitions)
//            consumer.seek(partition, readOffsetFromExternalStore(partition));

        final String id = Thread.currentThread().getId() + " ";
        System.out.println(MessageFormat.format("{0}--topicPartition--{1}", id, partitions));
        System.out.println(MessageFormat.format("分区偏移量表中:{0}", partitionOffsetMap));
        for (TopicPartition topicPartition : partitions) {
            System.out.println(MessageFormat.format("{0}--topicPartition{1}", id, topicPartition));
            Long offset = partitionOffsetMap.get(topicPartition);
            if (offset == null) continue;
            consumer.seek(topicPartition, partitionOffsetMap.get(topicPartition));
            consumer.seek(topicPartition, partitionOffsetMap.get(topicPartition));

        }

    }

}
