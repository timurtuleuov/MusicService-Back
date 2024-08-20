package com.timur.spotify.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
//    private static final String TOPIC = "auth-topic";

    public void sendMessage(String TOPIC, String message){
        kafkaTemplate.send(TOPIC, message);
    }
}