package com.timur.spotify.dto;

import com.timur.spotify.entity.music.Track;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class TrackFeedDTO {
    private Long id;
    private String title;
    private String description;

    private List<TrackDTO> tracks;
}