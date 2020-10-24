package me.minjun.springkafka.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 *  Kafka의 topic에 관한 설정
 *
 *  -  KafkaAdmin
 */

@Configuration
@Slf4j
public class KafkaTopicConfig {

//    @Value("${kafka.bootstrapAddress}")
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${message.topic.name}")
    private String topicName;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1(){
        log.info("Created topic : " + topicName);
        return new NewTopic(topicName, 1, (short) 1);
    }
}
