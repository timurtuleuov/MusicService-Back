package com.timur.spotify.entity.music;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracks/{trackId}/likes")
public class LikeController {
}
