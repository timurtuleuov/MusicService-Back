package com.timur.spotify.service.redis;

import com.timur.spotify.entity.Track;
import com.timur.spotify.service.TrackService;
import jakarta.persistence.Cacheable;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashedTracksService {
    private final TrackService trackService;
    @Cacheable(value = "popularTracks")
    public List<Track> getPopularTracks(){
        List<Track> popularTracks = new ArrayList<>();
        popularTracks.add(this.trackService.getTrackById(1L).get());
        popularTracks.add(this.trackService.getTrackById(2L).get());
        return popularTracks;
    }
}
