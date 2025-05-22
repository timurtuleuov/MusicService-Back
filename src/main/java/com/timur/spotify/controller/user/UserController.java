package com.timur.spotify.controller.user;

import com.timur.spotify.entity.auth.UserMeta;
import com.timur.spotify.service.auth.UserMetaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserMetaService userMetaService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{userId}")
    public ResponseEntity<UserMeta> getUserMeta(@PathVariable Long userId) {
        logger.info("Получен запрос на получение профиля пользователя с ID: {}", userId);
        return userMetaService.findByUserId(userId)
                .map(meta -> {
                    logger.debug("Найден профиль: {}", meta);
                    return ResponseEntity.ok(meta);
                })
                .orElseGet(() -> {
                    logger.warn("Профиль пользователя с ID {} не найден", userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<UserMeta> createOrUpdate(@RequestBody UserMeta userMeta) {
        logger.info("Запрос на создание/обновление профиля: {}", userMeta);
        UserMeta savedMeta = userMetaService.save(userMeta);
        logger.debug("Профиль сохранён: {}", savedMeta);
        return ResponseEntity.ok(savedMeta);
    }

    @PatchMapping("/{userId}/privacy")
    public ResponseEntity<Void> updatePrivacy(
            @PathVariable Long userId,
            @RequestParam boolean showProfile,
            @RequestParam boolean showPlaylist
    ) {
        logger.info("Запрос на обновление приватности для userId={}: showProfile={}, showPlaylist={}",
                userId, showProfile, showPlaylist);
        boolean updated = userMetaService.updatePrivacy(userId, showProfile, showPlaylist);
        if (updated) {
            logger.debug("Приватность успешно обновлена для пользователя {}", userId);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Не удалось обновить приватность. Пользователь {} не найден", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<Void> uploadAvatar(@PathVariable Long userId, @RequestParam MultipartFile file) {
        logger.info("Загрузка аватара для пользователя {}", userId);
        try {
            byte[] avatarBytes = file.getBytes();
            userMetaService.updateAvatar(userId, avatarBytes);
            logger.debug("Аватар успешно обновлён для пользователя {}", userId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error("Ошибка при загрузке аватара для пользователя {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long userId) {
        logger.info("Удаление аватара пользователя {}", userId);
        boolean removed = userMetaService.removeAvatar(userId);
        if (removed) {
            logger.debug("Аватар успешно удалён у пользователя {}", userId);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Не удалось удалить аватар — пользователь {} не найден", userId);
            return ResponseEntity.notFound().build();
        }
    }
}
