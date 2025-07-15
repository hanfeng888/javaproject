package com.smf.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * SendInfo TODO
 *
 * @author hf
 * @date 2024/1/7 14:53:13
 */
@Component
public class SendInfo implements ProducerListener {
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        System.out.println(MessageFormat.format("主题:{0},分区:{1},偏移量:{2},key:{3},value:{4}",
                producerRecord.topic(), recordMetadata.partition(), recordMetadata.offset(), producerRecord.key(), producerRecord.value()));
    }

    @Override
    public void onError(ProducerRecord producerRecord, RecordMetadata recordMetadata, Exception exception) {
        exception.printStackTrace();
    }
}
