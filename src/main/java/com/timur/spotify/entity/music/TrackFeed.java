package com.timur.spotify.entity.music;

import jakarta.persistence.*;
import lombok.Data;


import java.util.List;

@Data
@Entity
public class TrackFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToMany
    private List<Track> tracks;
}