package com.timur.spotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableCaching
public class SpotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyApplication.class, args);
	}

}
