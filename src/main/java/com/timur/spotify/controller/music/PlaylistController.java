package com.timur.spotify.controller.music;

import com.timur.spotify.dto.PlaylistDTO;
import com.timur.spotify.dto.PlaylistShortDTO;
import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.entity.music.PlaylistTrack;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.service.auth.JwtService;
import com.timur.spotify.service.auth.UserService;
import com.timur.spotify.service.music.FileStorageService;
import com.timur.spotify.service.music.PlaylistLikeService;
import com.timur.spotify.service.music.PlaylistService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);

    private final PlaylistService playlistService;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;

    public PlaylistController(PlaylistService playlistService, JwtService jwtService, FileStorageService fileStorageService) {
        this.playlistService = playlistService;
        this.jwtService = jwtService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaylistDTO> createPlaylist(
            @RequestPart("playlist") PlaylistDTO playlistDTO,
            @RequestPart(value = "cover", required = false) MultipartFile cover,
            HttpServletRequest request) throws IOException {

        logger.info("OPERATION: Creating playlist with name {}", playlistDTO.getName());

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must contain Bearer token");
        }
        token = token.substring(7);

        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in token");
        }

        // обработка cover, например, сохранить файл и получить URL
        String coverUrl = null;
        if (cover != null && !cover.isEmpty()) {
            coverUrl = fileStorageService.savePlaylistCover(cover); // реализуй fileStorageService
        }

        Playlist playlist = convertToEntity(playlistDTO, userId);
        playlist.setCover(coverUrl);

        PlaylistDTO created = playlistService.createPlaylist(playlist);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    // Вспомогательный метод для преобразования DTO в сущность
    private Playlist convertToEntity(PlaylistDTO playlistDTO, Long userId) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistDTO.getName());
        playlist.setCover(playlistDTO.getCover());
        playlist.setPrivate(playlistDTO.isPrivate());

        User user = new User();
        user.setId(userId);
        playlist.setUser(user);

        if (playlistDTO.getPlaylistTrackList() != null) {
            List<PlaylistTrack> playlistTracks = playlistDTO.getPlaylistTrackList().stream().map(trackDTO -> {
                PlaylistTrack track = new PlaylistTrack();
                track.setOrderInPlaylist(trackDTO.getOrderInPlaylist());
                track.setAddedAt(trackDTO.getAddedAt() != null ? trackDTO.getAddedAt() : LocalDateTime.now());

                Track referencedTrack = new Track();
                referencedTrack.setId(trackDTO.getTrackId()); // Только ID, остальное загружается из БД
                track.setTrack(referencedTrack);

                track.setPlaylist(playlist); // Прямая ссылка

                return track;
            }).collect(Collectors.toList());
            playlist.setPlaylistTracks(playlistTracks);
        }

        return playlist;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        logger.info("OPERATION: Getting playlist with id {}", id);
        PlaylistDTO playlist = playlistService.getById(id);
        if (playlist != null) {
            logger.info("SUCCESS: Found playlist with id {}", id);
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        } else {
            logger.error("FAIL: Not found playlist with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/author/{userId}")
    public ResponseEntity<List<PlaylistShortDTO>> getPlaylistsByAuthorId(@PathVariable Long userId) {
        logger.info("OPERATION: Getting playlist by author id {}", userId);
        List<PlaylistShortDTO> playlists = playlistService.getAllPlaylistByAuthorId(userId);
        logger.info("SUCCESS: Found playlist by author id {}", userId);
        return new ResponseEntity<>(playlists, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Playlist> updatePlaylist(
            @PathVariable Long id,
            @RequestPart("playlist") PlaylistDTO playlistDTO,
            @RequestPart(value = "cover", required = false) MultipartFile cover,
            HttpServletRequest request
    ) throws IOException {
        logger.info("OPERATION: Updating playlist with id {} and name {}", id, playlistDTO.getName());
        // Извлечение токена из заголовка
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must contain Bearer token");
        }
        token = token.substring(7); // Удаляем "Bearer "

        // Извлечение userId из токена
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in token");
        }

        String coverUrl = null;
        if (cover != null && !cover.isEmpty()) {
            coverUrl = fileStorageService.savePlaylistCover(cover); // реализуй fileStorageService
        }
        Playlist playlist = convertToEntity(playlistDTO, userId);
        playlist.setCover(coverUrl);

        logger.info("FULL Playlist for updating {}", playlist);
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlist);
        if (updatedPlaylist != null) {
            logger.info("SUCCESS: Updated playlist with id {}", id);
            return new ResponseEntity<>(updatedPlaylist, HttpStatus.OK);
        } else {
            logger.info("FAIL: Not found playlist with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        logger.info("OPERATION: Deleting playlist with id {}", id);
        boolean isDeleted = playlistService.deletePlaylist(id);
        if (isDeleted) {
            logger.info("SUCCESS: Deleted playlist with id {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.info("FAIL: Not found playlist with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RestController
    @RequestMapping("/playlists/{playlistId}/likes")
    public static class PlaylistLikeController {

        private final PlaylistLikeService likeService;
        private final PlaylistService playlistService;
        private final UserService userService;

        public PlaylistLikeController(PlaylistLikeService likeService, PlaylistService playlistService, UserService userService) {
            this.likeService = likeService;
            this.playlistService = playlistService;
            this.userService = userService;
        }

//        @PostMapping
//        public ResponseEntity<Void> likePlaylist(@PathVariable Long playlistId, @AuthenticationPrincipal User user) {
//            Playlist playlist = playlistService.getById(playlistId);
//            likeService.likePlaylist(user, playlist);
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        }
//
//        @DeleteMapping
//        public ResponseEntity<Void> unlikePlaylist(@PathVariable Long playlistId, @AuthenticationPrincipal User user) {
//            Playlist playlist = playlistService.getById(playlistId);
//            likeService.unlikePlaylist(user, playlist);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        @GetMapping("/count")
//        public ResponseEntity<Long> getLikeCount(@PathVariable Long playlistId) {
//            Playlist playlist = playlistService.getById(playlistId);
//            long likeCount = likeService.countLikes(playlist);
//            return new ResponseEntity<>(likeCount, HttpStatus.OK);
//        }
    }

}
