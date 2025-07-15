package com.smf.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * SelfInterceptor TODO
 *
 * @author hf
 * @date 2024/1/5 11:33:45
 */
public class SelfInterceptor implements ProducerInterceptor<String, String> {

    private long successCount = 0;
    private long errorCount = 0;

    //Producer在将消息序列化和分配分区之前会调用拦截器的这个方法来对消息进行相应的操作
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {

        //要把发送的数据都带上时间戳
        return new ProducerRecord<String, String>(
                record.topic(),
                record.partition(),
                record.timestamp(),
                record.key(),
                record.value(),
                new ArrayList<>()
        );
    }

    //发送消息情况统计
    //该方法：会在消息从RecordAccumulator成功发送到Kafka Broker之后，或者在发送过程中失败时调用。
    //并且通过都是在producer回调逻辑触发之前
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            successCount++;
        } else {
            errorCount++;
        }
    }

    //该方法：可以关闭拦截器，主要用于执行一些资源清理工作
    @Override
    public void close() {
        //producer发送数据结束并close后，会自动调用拦截器的close方法来输出统计的成功和失败次数
        System.out.println("成功次数="+successCount);
        System.out.println("失败次数="+errorCount);
    }

    @Override
    public void configure(Map<String, ?> configs) {
        //配置变更增强
    }
}
