package com.timur.spotify.controller.music;

import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.service.music.PlaylistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist) {
        logger.info("OPERATION: Creating playlist with name {} by author", playlist.getName(), playlist.getAuthor().getUsername());
        Playlist createdPlaylist = playlistService.createPlaylist(playlist);
        return new ResponseEntity<>(createdPlaylist, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Long id) {
        logger.info("OPERATION: Getting playlist with id {}", id);
        Playlist playlist = playlistService.getById(id);
        if (playlist != null) {
            logger.info("SUCCESS: Found playlist with id {}", id);
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        } else {
            logger.error("FAIL: Not found playlist with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Playlist>> getAllPlaylistsByAuthorId(@PathVariable Long authorId) {
        logger.info("OPERATION: Getting playlist by author id {}", authorId);
        List<Playlist> playlists = playlistService.getAllPlaylistByAuthorId(authorId);
        if (!playlists.isEmpty()) {
            logger.info("SUCCESS: Found playlist by author id {}", authorId);
            return new ResponseEntity<>(playlists, HttpStatus.OK);
        } else {
            logger.info("SUCCESS: Not found playlist by author id {}", authorId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        logger.info("OPERATION: Updating playlist with id {} and name {}", id, playlist.getName());
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
}
