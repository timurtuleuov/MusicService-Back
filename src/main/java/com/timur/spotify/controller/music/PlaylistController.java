package com.timur.spotify.controller.music;

import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.service.music.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist) {
        Playlist createdPlaylist = playlistService.createPlaylist(playlist);
        return new ResponseEntity<>(createdPlaylist, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.getById(id);
        if (playlist != null) {
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Playlist>> getAllPlaylistsByAuthorId(@PathVariable Long authorId) {
        List<Playlist> playlists = playlistService.getAllPlaylistByAuthorId(authorId);
        if (!playlists.isEmpty()) {
            return new ResponseEntity<>(playlists, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlist);
        if (updatedPlaylist != null) {
            return new ResponseEntity<>(updatedPlaylist, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
