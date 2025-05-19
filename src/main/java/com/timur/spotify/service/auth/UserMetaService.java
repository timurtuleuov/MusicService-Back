package com.timur.spotify.service.auth;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.auth.UserMeta;
import com.timur.spotify.repository.auth.UserMetaRepository;
import com.timur.spotify.repository.auth.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMetaService {

    private final UserMetaRepository userMetaRepository;
    private final UserRepository userRepository;

    public Optional<UserMeta> getByUserId(Long userId) {
        return userMetaRepository.findByUserId(userId);
    }

    @Transactional
    public UserMeta createOrGetByUser(User user) {
        return userMetaRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserMeta meta = new UserMeta();
                    meta.setUser(user);
                    return userMetaRepository.save(meta);
                });
    }

    @Transactional
    public void updateAvatar(Long userId, byte[] avatarData) {
        UserMeta meta = userMetaRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserMeta not found"));

        meta.setAvatar(avatarData);
        userMetaRepository.save(meta);
    }

    @Transactional
    public void updatePrivacySettings(Long userId, boolean showProfile, boolean showPlaylist) {
        UserMeta meta = userMetaRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserMeta not found"));

        meta.setShowProfile(showProfile);
        meta.setShowPlaylist(showPlaylist);
        userMetaRepository.save(meta);
    }
}