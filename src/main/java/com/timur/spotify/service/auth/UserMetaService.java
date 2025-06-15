package com.timur.spotify.service.auth;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.auth.UserMeta;
import com.timur.spotify.repository.auth.UserMetaRepository;
import com.timur.spotify.repository.auth.UserRepository;
import com.timur.spotify.service.music.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMetaService {

    private final UserMetaRepository userMetaRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

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
    public Optional<UserMeta> findByUserId(Long userId){
        return userMetaRepository.findByUserId(userId);
    }

    @Transactional
    public String updateAvatar(Long userId, MultipartFile avatarFile) throws IOException {
        UserMeta meta = userMetaRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("UserMeta not found"));

        String avatarUrl = fileStorageService.saveUserAvatar(avatarFile); // "/avatars/uuid-filename.jpg"
        meta.setAvatar(avatarUrl);
        userMetaRepository.save(meta);

        return avatarUrl;
    }
    @Transactional
    public boolean updatePrivacy(Long userId, boolean showProfile, boolean showPlaylist) {
        try {
            UserMeta meta = userMetaRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("UserMeta not found"));

            meta.setShowProfile(showProfile);
            meta.setShowPlaylist(showPlaylist);
            userMetaRepository.save(meta);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean removeAvatar(Long userId) {
        return userMetaRepository.findByUserId(userId).map(meta -> {
            meta.setAvatar(null);
            userMetaRepository.save(meta);
            return true;
        }).orElse(false);
    }

    @Transactional
    public UserMeta save(UserMeta userMeta) {
        return userMetaRepository.save(userMeta);
    }

}
