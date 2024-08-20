package com.timur.spotify.controller.auth;

import com.timur.spotify.controller.music.AlbumController;
import com.timur.spotify.dto.JwtAuthenticationResponse;
import com.timur.spotify.dto.SignInRequest;
import com.timur.spotify.dto.SignUpRequest;
import com.timur.spotify.service.auth.AuthService;
import com.timur.spotify.service.kafka.ProducerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final ProducerService kafkaProducer;

    public AuthController(AuthService authService, ProducerService kafkaProducer) {
        this.authService = authService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        logger.info("OPERATION: User {} signing up", request.getUsername());
        JwtAuthenticationResponse response = authService.signUp(request);
        this.kafkaProducer.sendMessage("auth-topic", "User signed up: " + request.getUsername());
        return response;
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        logger.info("OPERATION: User {} signing in", request.getUsername());
        JwtAuthenticationResponse response = authService.signIn(request);
        this.kafkaProducer.sendMessage("auth-topic", "User signed in: " + request.getUsername());
        return response;
    }
}