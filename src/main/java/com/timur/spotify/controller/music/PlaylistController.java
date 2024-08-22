package com.timur.spotify.controller.music;

import com.timur.spotify.service.music.PlaylistService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;
    
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}
