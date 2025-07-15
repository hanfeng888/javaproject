package com.smf.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaProducerConfig TODO
 *
 * @author hf
 * @date 2024/1/7 14:12:12
 */
@Configuration
@EnableKafka
public class KafkaProducerConfig {


    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.retries}")
    private int retries;
    @Value("${spring.kafka.producer.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.producer.buffer-memory}")
    private int bufferMemory;
    @Value("${spring.kafka.producer.linger}")
    private int linger;
    @Value("${spring.kafka.producer.acks}")
    private int acks;
    @Value("${spring.kafka.producer.partition-number}")
    private int partitionNumber;
    @Value("${spring.kafka.producer.replication-factor}")
    private int replicationFactor;
    /**
     * 配置元数据的过期时间，默认值为300000 ( ms ），即5 分钟。如果元数据在
     * 此参数所限定的时间范围内没有进行更新，则会被强制更新，即使没有任何分区变化或有新的
     * broker 加入
     */
    @Value("${spring.kafka.producer.metadata-max-age-ms}")
    private int metadataMaxAgeMs;

//    @Value("${spring.kafka.producer.key-serializer}")
//    private String keySerializer;
//    @Value("${spring.kafka.producer.value-serializer}")
//    private String valueSerializer;

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(){
        KafkaTemplate kafkaTemplate=new KafkaTemplate(producerFactory());
//        kafkaTemplate.setProducerListener();
        kafkaTemplate.setTransactionIdPrefix("vts");
        return kafkaTemplate;
    }
}

