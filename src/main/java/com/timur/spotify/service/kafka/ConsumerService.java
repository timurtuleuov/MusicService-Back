package com.timur.spotify.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @KafkaListener(topics = "auth-topic", groupId = "group_id")
    public void consume(String message){
        System.out.println("Message from AuthSystem: " + message);
    }
}
