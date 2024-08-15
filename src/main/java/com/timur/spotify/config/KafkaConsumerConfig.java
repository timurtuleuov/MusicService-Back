package com.timur.spotify.config;

import com.timur.spotify.controller.music.AlbumController;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;


import java.util.HashMap;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);
    private final String bootstrapServers = "localhost:9092";
    @CircuitBreaker(name="kafkaConsumer", fallbackMethod = "fallbackMethod")
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(){
        HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @CircuitBreaker(name="kafkaConsumer", fallbackMethod = "fallbackMethod")
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    public void fallbackMethod(String message, Throwable t) {
        logger.error("Fallback for ConsumerService with message {}: {}", message, t.getMessage());
    }
}
