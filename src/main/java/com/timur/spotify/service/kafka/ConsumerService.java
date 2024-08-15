package com.timur.spotify.service.kafka;

import com.timur.spotify.controller.music.AlbumController;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
//    @KafkaListener(topics = "auth-topic", groupId = "group_id")
//    public void consume(String message){
//        System.out.println("Message from AuthSystem: " + message);
//    }

}
