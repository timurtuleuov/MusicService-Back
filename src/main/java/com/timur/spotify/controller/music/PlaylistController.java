package com.timur.spotify.controller.music;

import com.timur.spotify.dto.PlaylistDTO;
import com.timur.spotify.dto.PlaylistShortDTO;
import com.timur.spotify.entity.auth.User;
import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.entity.music.PlaylistTrack;
import com.timur.spotify.entity.music.Track;
import com.timur.spotify.service.auth.JwtService;
import com.timur.spotify.service.auth.UserService;
import com.timur.spotify.service.music.PlaylistLikeService;
import com.timur.spotify.service.music.PlaylistService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);

    private final PlaylistService playlistService;
    private final JwtService jwtService;

    public PlaylistController(PlaylistService playlistService, JwtService jwtService) {
        this.playlistService = playlistService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO, HttpServletRequest request) {
        logger.info("OPERATION: Received request to create playlist");
        logger.info("Playlist details: {}",
                playlistDTO.getPlaylistTrackList());

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

        // Преобразование DTO в сущность Playlist для сохранения
        Playlist playlist = convertToEntity(playlistDTO, userId);

        logger.info("Author details: id={}", playlist.getUser().getId());

        logger.info("OPERATION: Creating playlist with name {} by user {}",
                playlist.getName(), playlist.getUser().getId());
        PlaylistDTO createdPlaylistDTO = playlistService.createPlaylist(playlist);
        logger.info("SUCCESS: Playlist created with id {}", createdPlaylistDTO.getId());

        return new ResponseEntity<>(createdPlaylistDTO, HttpStatus.CREATED);
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

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody PlaylistDTO playlistDTO, HttpServletRequest request) {
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
        Playlist playlist = convertToEntity(playlistDTO, userId);

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
