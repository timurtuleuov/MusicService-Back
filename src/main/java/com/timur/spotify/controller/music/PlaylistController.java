package com.timur.spotify.controller.music;

import com.timur.spotify.entity.music.Playlist;
import com.timur.spotify.service.music.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
