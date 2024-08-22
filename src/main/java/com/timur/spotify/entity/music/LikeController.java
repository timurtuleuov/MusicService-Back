package com.timur.spotify.entity.music;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.service.auth.UserService;
import com.timur.spotify.service.music.LikeService;
import com.timur.spotify.service.music.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracks/{trackId}/likes")
public class LikeController {

    private final LikeService likeService;
    private final TrackService trackService;
    private final UserService userService;

    public LikeController(LikeService likeService, TrackService trackService, UserService userService) {
        this.likeService = likeService;
        this.trackService = trackService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> likeTrack(@PathVariable Long trackId, @AuthenticationPrincipal User user) {
        Track track = trackService.getTrackById(trackId);
        likeService.likeTrack(user, track);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> unlikeTrack(@PathVariable Long trackId, @AuthenticationPrincipal User user) {
        Track track = trackService.getTrackById(trackId);
        likeService.unlikeTrack(user, track);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long trackId) {
        Track track = trackService.getTrackById(trackId);
        long likeCount = likeService.countLikes(track);
        return new ResponseEntity<>(likeCount, HttpStatus.OK);
    }
}
