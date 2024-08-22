package com.timur.spotify.entity.music;

import com.timur.spotify.service.auth.UserService;
import com.timur.spotify.service.music.LikeService;
import com.timur.spotify.service.music.TrackService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
