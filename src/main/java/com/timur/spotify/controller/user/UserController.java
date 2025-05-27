package com.timur.spotify.controller.user;

import com.timur.spotify.dto.UserMetaDTO;
import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.auth.UserMeta;
import com.timur.spotify.service.auth.UserMetaService;
import com.timur.spotify.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserMetaService userMetaService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{userId}")
    public ResponseEntity<UserMetaDTO> getUserMeta(@PathVariable Long userId) {
        logger.info("Получен запрос на получение профиля пользователя с ID: {}", userId);

        Optional<UserMeta> userMetaOpt = userMetaService.findByUserId(userId);

        if (userMetaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserMeta user = userMetaOpt.get();

        UserMetaDTO dto = new UserMetaDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUser().getUsername());
        dto.setEmail(user.getUser().getEmail());
        if (user.getAvatar() != null) {
            String base64Avatar = java.util.Base64.getEncoder().encodeToString(user.getAvatar());
            dto.setAvatar((base64Avatar));
        } else {
            dto.setAvatar(null);
        }
        dto.setShowPlaylist(user.isShowPlaylist());
        dto.setShowProfile(user.isShowProfile());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserMetaDTO> createOrUpdate(@RequestBody UserMetaDTO dto) {
        logger.info("Запрос на создание/обновление профиля: {}", dto.getUsername());

        if (dto.getUsername() == null) {
            logger.error("Имя пользователя не указано");
            return ResponseEntity.badRequest().build();
        }

        User userOpt = userService.getByUsername(dto.getUsername());

        // Найдём существующий UserMeta или создадим новый
        Optional<UserMeta> existingMetaOpt = userMetaService.findByUserId(userOpt.getId());
        UserMeta userMeta = existingMetaOpt.orElseGet(UserMeta::new);

        userMeta.setUser(userOpt);
        userMeta.setAvatar(dto.getAvatar().getBytes());
        userMeta.setShowProfile(dto.isShowProfile());
        userMeta.setShowPlaylist(dto.isShowPlaylist());

        UserMeta savedMeta = userMetaService.save(userMeta);
        logger.info("Профиль сохранён: {}", savedMeta);

        UserMetaDTO savedDto = new UserMetaDTO();
        savedDto.setId(savedMeta.getId());
        savedDto.setAvatar(Arrays.toString(savedMeta.getAvatar()));
        savedDto.setShowProfile(savedMeta.isShowProfile());
        savedDto.setShowPlaylist(savedMeta.isShowPlaylist());

        return ResponseEntity.ok(savedDto);
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
    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId,
                                               @RequestParam MultipartFile file) {
        logger.info("Загрузка аватара для пользователя {}", userId);

        try {
            if (file.isEmpty()) {
                logger.error("Пустой файл для пользователя {}", userId);
                return ResponseEntity.badRequest().body("Файл пустой");
            }

            String contentType = file.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                logger.error("Недопустимый формат файла: {}", contentType);
                return ResponseEntity.badRequest().body("Допустимы только JPEG и PNG изображения");
            }
            Optional<UserMeta> existingMetaOpt = userMetaService.findByUserId(userId);
            UserMeta userMeta = existingMetaOpt.orElseGet(UserMeta::new);
            byte[] avatarBytes = file.getBytes();

            userMeta.setAvatar(avatarBytes);
            userMetaService.save(userMeta);
            logger.info("Аватар успешно обновлён для пользователя {}", userId);
            return ResponseEntity.ok("data:" + contentType + ";base64,"  );
        } catch (IOException e) {
            logger.error("Ошибка при загрузке аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении файла");
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