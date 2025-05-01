package com.timur.spotify.controller.music;

import com.timur.spotify.dto.TrackDTO;
import com.timur.spotify.entity.music.TrackLike;
import com.timur.spotify.service.music.TrackLikeService;
import com.timur.spotify.service.music.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private  TrackService trackService;


    @GetMapping
    public List<TrackDTO> search(@RequestParam("query") String query, @RequestParam("userId") Long userId) {
        List<TrackDTO> tracks = trackService.searchTracks(query, userId);
        return tracks;
    }
}
