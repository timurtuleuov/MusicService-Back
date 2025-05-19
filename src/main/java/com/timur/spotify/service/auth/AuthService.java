package com.timur.spotify.service.auth;

import com.timur.spotify.controller.auth.AuthController;
import com.timur.spotify.dto.JwtAuthenticationResponse;
import com.timur.spotify.dto.SignInRequest;
import com.timur.spotify.dto.SignUpRequest;
import com.timur.spotify.entity.auth.Role;
import com.timur.spotify.entity.auth.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public AuthService(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.save(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        logger.info("Получаем юзера {} ", user);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);

        if (!"refresh".equals(claims.get("type"))) {
            throw new JwtException("Not a refresh token");
        }

        String username = claims.getSubject();
        UserDetails userDetails = userService.getByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);
        // необязательно, по желанию

        return JwtAuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
